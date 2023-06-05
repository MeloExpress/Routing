package br.com.MeloExpress.Routing.Service;

import br.com.MeloExpress.Routing.DTO.*;
import br.com.MeloExpress.Routing.Domain.*;
import br.com.MeloExpress.Routing.Repository.RoutingCollectRepository;
import br.com.MeloExpress.Routing.Repository.RoutingEmployeeRepository;
import br.com.MeloExpress.Routing.Repository.RoutingFleetRepository;
import br.com.MeloExpress.Routing.Repository.RoutingRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoutingService {

    @Value("${employee.api.url}")
    private String employeeApiUrl;

    @Value("${fleet.api.url}")
    private String fleetApiUrl;

    @Value("${collect.api.url}")
    private String collectApiUrl;

    @Value("${collect.api.url.status}")
    private String collectApiUrlStatus;

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private RoutingCollectRepository routingCollectRepository;

    @Autowired
    private RoutingFleetRepository routingFleetRepository;

    @Autowired
    private RoutingEmployeeRepository routingEmployeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public RoutingAllDetailsToCreateDTO createRouting(RoutingCreateDTO routingCreateDTO, String employeeApiUrl, String fleetApiUrl, String collectApiUrl) {
        // Recuperando dados de funcionario da API Employee
        String employeeApiPath = employeeApiUrl + routingCreateDTO.routingEmployeeCode();
        ResponseEntity<RoutingEmployeeDetailsFindDTO> employeeResponse = restTemplate.getForEntity(employeeApiPath, RoutingEmployeeDetailsFindDTO.class);
        RoutingEmployeeDetailsFindDTO employeeDetails = employeeResponse.getBody();

        // Persistindo dados de funcionario na tabela routing_employee
        RoutingEmployee routingEmployee = new RoutingEmployee();
        routingEmployee.setEmployeeCode(employeeDetails.employeeCode());
        routingEmployeeRepository.save(routingEmployee);

        // Recuperando dados da frota da API Fleet
        String fleetApiPath = fleetApiUrl + routingCreateDTO.routingFleetCode();
        ResponseEntity<RoutingFleetDetailsFindDTO> fleetResponse = restTemplate.getForEntity(fleetApiPath, RoutingFleetDetailsFindDTO.class);
        RoutingFleetDetailsFindDTO fleetDetails = fleetResponse.getBody();

        // Persistindo dados da frota na tabela routing_fleet
        RoutingFleet routingFleet = new RoutingFleet();
        routingFleet.setFleetCode(fleetDetails.fleetCode());
        routingFleetRepository.save(routingFleet);

        // Recuperando dados da coleta da API Collect
        List<RoutingCollect> routingCollects = routingCreateDTO.routingCollects();
        List<RoutingCollectDetailsFindDTO> collectDetailsList = new ArrayList<>();

        for (RoutingCollect routingCollect : routingCollects) {
            String collectApiPath = collectApiUrl + routingCollect.getRoutingCollectCode();
            ResponseEntity<RoutingCollectDetailsFindDTO> collectResponse = restTemplate.getForEntity(collectApiPath, RoutingCollectDetailsFindDTO.class);
            RoutingCollectDetailsFindDTO collectDetails = collectResponse.getBody();
            collectDetailsList.add(collectDetails);
        }

        // Persistindo dados da coleta na tabela routing_collect
        List<RoutingCollect> routingCollectList = new ArrayList<>();
        for (RoutingCollectDetailsFindDTO collectDetails : collectDetailsList) {
            RoutingCollect routingCollect = new RoutingCollect();
            routingCollect.setRoutingCollectCode(collectDetails.collectCode());
            routingCollectList.add(routingCollect);
        }
        routingCollectRepository.saveAll(routingCollectList);

        // Criando objeto de roteiro com as informações recuperadas e persistidas
        Routing routing = new Routing();
        routing.setRoutingCode(UUID.randomUUID());
        routing.setReason(Reason.CREATE);
        routing.setRoutingEmployee(routingEmployee.getEmployeeCode());
        routing.setRoutingFleet(routingFleet.getFleetCode());
        routing.setRoutingCollect(routingCollectList);
        routingRepository.save(routing);

        // Retornando os dados recuperados para vizualização
        RoutingAllDetailsToCreateDTO response = new RoutingAllDetailsToCreateDTO(
                routing.getReason(),
                routing.getRoutingId(),
                routing.getRoutingCode(),
                employeeDetails,
                fleetDetails,
                collectDetailsList
        );

        //Criando a mensagem JSON para o Kafka
        String routingCode = routing.getRoutingCode().toString();
        JSONObject mensagem = new JSONObject();
        mensagem.put("routingId", routing.getRoutingId());
        mensagem.put("routingCode", routingCode);
        mensagem.put("employeeDetails", employeeDetails);
        mensagem.put("fleetDetails", fleetDetails);

        List<RoutingCollect> routingCollectListKafka = routing.getRoutingCollect();
        JSONArray collectDetailsListKafka = new JSONArray();
        for (RoutingCollect collect : routingCollectListKafka) {
            JSONObject collectDetails = new JSONObject();
            collectDetails.put("routingCollectId", collect.getRoutingCollectId());
            collectDetails.put("routingCollectCode", collect.getRoutingCollectCode());
            collectDetailsListKafka.put(collectDetails);
        }
        mensagem.put("collectDetailsList", collectDetailsListKafka);


        //Criando a mensagem com o payload
        Message<String> kafkaMessage = MessageBuilder
                .withPayload(mensagem.toString())
                .build();

        //Enviando mensagem para o tópico Kafka
        kafkaTemplate.send("routing-event", routingCode, kafkaMessage.toString());

        return response;

    }

    public RoutingAllDetailsToCreateDTO addCollectsToRouting(UUID routingCode, List<RoutingCollect> newCollects, String collectApiUrl) {
    // Recuperando o roteiro existente pelo code
    Optional<Routing> existingRoutingOptional = routingRepository.findByRoutingCode(routingCode);
    if (existingRoutingOptional.isEmpty()) {
        throw new IllegalArgumentException("Roteiro não encontrado.");
    }
    Routing existingRouting = existingRoutingOptional.get();

    // Recuperando os detalhes do roteiro existente
    RoutingEmployeeDetailsFindDTO employeeDetails = restTemplate.getForObject(employeeApiUrl + existingRouting.getRoutingEmployee(), RoutingEmployeeDetailsFindDTO.class);
    RoutingFleetDetailsFindDTO fleetDetails = restTemplate.getForObject(fleetApiUrl + existingRouting.getRoutingFleet(), RoutingFleetDetailsFindDTO.class);
    List<RoutingCollectDetailsFindDTO> existingCollectDetailsList = new ArrayList<>();
    for (RoutingCollect routingCollect : existingRouting.getRoutingCollect()) {
        String collectApiPath = collectApiUrl + routingCollect.getRoutingCollectCode();
        RoutingCollectDetailsFindDTO collectDetails = restTemplate.getForObject(collectApiPath, RoutingCollectDetailsFindDTO.class);
        existingCollectDetailsList.add(collectDetails);
    }

    // Recuperando os detalhes das novas coletas da API Collect
    List<RoutingCollectDetailsFindDTO> newCollectDetailsList = new ArrayList<>();
    for (RoutingCollect newCollect : newCollects) {
        boolean collectExists = false;
        String collectApiPath = collectApiUrl + newCollect.getRoutingCollectCode();
        RoutingCollectDetailsFindDTO collectDetails = restTemplate.getForObject(collectApiPath, RoutingCollectDetailsFindDTO.class);

        // Verificando se a coleta já existe no roteiro
        for (RoutingCollectDetailsFindDTO existingCollect : existingCollectDetailsList) {
            if (existingCollect.collectCode().equals(collectDetails.collectCode())) {
                collectExists = true;
                break;
            }
        }

        // Se a coleta não existe, adiciona aos detalhes das novas coletas
        if (!collectExists) {
            newCollectDetailsList.add(collectDetails);

            // Persistindo a nova coleta na tabela routing_collect
            RoutingCollect routingCollect = new RoutingCollect(routingCode.toString());
            routingCollect.setRoutingCollectCode(collectDetails.collectCode());
            routingCollectRepository.save(routingCollect);

            // Atualizando o roteamento existente com a nova coleta
            existingRouting.getRoutingCollect().add(routingCollect);
            routingRepository.save(existingRouting);
        }
    }

    // Retornando os detalhes atualizados do roteiro
    List<RoutingCollectDetailsFindDTO> updatedCollectDetailsList = new ArrayList<>(existingCollectDetailsList);
    updatedCollectDetailsList.addAll(newCollectDetailsList);

    RoutingAllDetailsToCreateDTO response1 = new RoutingAllDetailsToCreateDTO(
            existingRouting.getReason(),
            existingRouting.getRoutingId(),
            existingRouting.getRoutingCode(),
            employeeDetails,
            fleetDetails,
            updatedCollectDetailsList
    );

    return response1;
    }


    public List<RoutingCollectDetailsFindDTO> getAllCollectsByRouting(UUID routingCode, String collectApiUrl) {
        // Recuperando o roteiro existente pelo code
        Optional<Routing> existingRoutingOptional = routingRepository.findByRoutingCode(routingCode);
        if (existingRoutingOptional.isEmpty()) {
            throw new IllegalArgumentException("Roteiro não encontrado.");
        }
        Routing existingRouting = existingRoutingOptional.get();

        List<RoutingCollectDetailsFindDTO> collectDetailsList = new ArrayList<>();
        for (RoutingCollect routingCollect : existingRouting.getRoutingCollect()) {
            String collectApiPath = collectApiUrl + routingCollect.getRoutingCollectCode();
            RoutingCollectDetailsFindDTO collectDetails = restTemplate.getForObject(collectApiPath, RoutingCollectDetailsFindDTO.class);
            collectDetailsList.add(collectDetails);
        }

        return collectDetailsList;
    }

}
