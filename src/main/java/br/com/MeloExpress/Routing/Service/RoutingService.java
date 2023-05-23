package br.com.MeloExpress.Routing.Service;

import br.com.MeloExpress.Routing.DTO.*;
import br.com.MeloExpress.Routing.Domain.Routing;
import br.com.MeloExpress.Routing.Domain.RoutingCollect;
import br.com.MeloExpress.Routing.Domain.RoutingEmployee;
import br.com.MeloExpress.Routing.Domain.RoutingFleet;
import br.com.MeloExpress.Routing.Repository.RoutingCollectRepository;
import br.com.MeloExpress.Routing.Repository.RoutingEmployeeRepository;
import br.com.MeloExpress.Routing.Repository.RoutingFleetRepository;
import br.com.MeloExpress.Routing.Repository.RoutingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private RoutingCollectRepository routingCollectRepository;

    @Autowired
    private RoutingFleetRepository routingFleettRepository;

    @Autowired
    private RoutingEmployeeRepository routingEmployeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    public RoutingAllDetailsToCreateDTO createRouting(RoutingCreateDTO routingCreateDTO, String employeeApiUrl, String fleetApiUrl, String collectApiUrl) {
        // Recuperando dados de Employee da API Employee
        String employeeApiPath = employeeApiUrl + routingCreateDTO.routingEmployeeCode();
        ResponseEntity<RoutingEmployeeDetailsFindDTO> employeeResponse = restTemplate.getForEntity(employeeApiPath, RoutingEmployeeDetailsFindDTO.class);
        RoutingEmployeeDetailsFindDTO employeeDetails = employeeResponse.getBody();

        // Persistindo dados de Employee na tabela routing_employee
        RoutingEmployee routingEmployee = new RoutingEmployee();
        routingEmployee.setEmployeeCode(employeeDetails.employeeCode());
        routingEmployeeRepository.save(routingEmployee);

        // Recuperando dados de Fleet da API Fleet
        String fleetApiPath = fleetApiUrl + routingCreateDTO.routingFleetCode();
        ResponseEntity<RoutingFleetDetailsFindDTO> fleetResponse = restTemplate.getForEntity(fleetApiPath, RoutingFleetDetailsFindDTO.class);
        RoutingFleetDetailsFindDTO fleetDetails = fleetResponse.getBody();

        // Persistindo dados de Fleet na tabela routing_fleet
        RoutingFleet routingFleet = new RoutingFleet();
        routingFleet.setFleetCode(fleetDetails.fleetCode());
        routingFleettRepository.save(routingFleet);

        // Recuperando dados de Collect da API Collect
        List<RoutingCollect> routingCollects = routingCreateDTO.routingCollects();
        List<RoutingCollectDetailsFindDTO> collectDetailsList = new ArrayList<>();

        for (RoutingCollect routingCollect : routingCollects) {
            String collectApiPath = collectApiUrl + routingCollect.getRoutingCollectCode();
            ResponseEntity<RoutingCollectDetailsFindDTO> collectResponse = restTemplate.getForEntity(collectApiPath, RoutingCollectDetailsFindDTO.class);
            RoutingCollectDetailsFindDTO collectDetails = collectResponse.getBody();
            collectDetailsList.add(collectDetails);
        }

        // Persistindo dados de Collect na tabela routing_collect
        List<RoutingCollect> routingCollectList = new ArrayList<>();
        for (RoutingCollectDetailsFindDTO collectDetails : collectDetailsList) {
            RoutingCollect routingCollect = new RoutingCollect();
            routingCollect.setRoutingCollectCode(collectDetails.collectCode());
            routingCollectList.add(routingCollect);
        }
        routingCollectRepository.saveAll(routingCollectList);

        // Criando objeto de roteamento com as informações recuperadas e persistidas
        Routing routing = new Routing();
        routing.setRoutingCode(UUID.randomUUID());
        routing.setRoutingEmployee(routingEmployee.getEmployeeCode());
        routing.setRoutingFleet(routingFleet.getFleetCode());
        routing.setRoutingCollect(routingCollectList);
        routingRepository.save(routing);


        // Retornando os dados recuperados para vizualização
        RoutingAllDetailsToCreateDTO response = new RoutingAllDetailsToCreateDTO(
                routing.getRoutingId(),
                employeeDetails,
                fleetDetails,
                collectDetailsList
        );

        return response;

    }

    public RoutingAllDetailsToCreateDTO addCollectsToRouting(UUID routingCode, List<RoutingCollect> newCollects, String collectApiUrl) {
        // Recuperando o roteamento existente pelo código
        Optional<Routing> existingRoutingOptional = routingRepository.findByRoutingCode(routingCode);
        if (existingRoutingOptional.isEmpty()) {
            throw new IllegalArgumentException("Roteamento não encontrado.");
        }
        Routing existingRouting = existingRoutingOptional.get();

        // Recuperando os detalhes do roteamento existente
        RoutingEmployeeDetailsFindDTO employeeDetails = restTemplate.getForObject(employeeApiUrl + existingRouting.getRoutingEmployee(), RoutingEmployeeDetailsFindDTO.class);
        RoutingFleetDetailsFindDTO fleetDetails = restTemplate.getForObject(fleetApiUrl + existingRouting.getRoutingFleet(), RoutingFleetDetailsFindDTO.class);
        List<RoutingCollectDetailsFindDTO> existingCollectDetailsList = new ArrayList<>();
        for (RoutingCollect routingCollect : existingRouting.getRoutingCollect()) {
            String collectApiPath = collectApiUrl + routingCollect.getRoutingCollectCode();
            RoutingCollectDetailsFindDTO collectDetails = restTemplate.getForObject(collectApiPath, RoutingCollectDetailsFindDTO.class);
            existingCollectDetailsList.add(collectDetails);
        }

        // Recuperando os detalhes das novas coleções da API Collect
        List<RoutingCollectDetailsFindDTO> newCollectDetailsList = new ArrayList<>();
        for (RoutingCollect newCollect : newCollects) {
            String collectApiPath = collectApiUrl + newCollect.getRoutingCollectCode();
            RoutingCollectDetailsFindDTO collectDetails = restTemplate.getForObject(collectApiPath, RoutingCollectDetailsFindDTO.class);
            newCollectDetailsList.add(collectDetails);
        }

        // Persistindo as novas coleções na tabela routing_collect
        List<RoutingCollect> routingCollectList = new ArrayList<>();
        for (RoutingCollectDetailsFindDTO collectDetails : newCollectDetailsList) {
            RoutingCollect routingCollect = new RoutingCollect(routingCode.toString());
            routingCollect.setRoutingCollectCode(collectDetails.collectCode());
            routingCollectList.add(routingCollect);
        }
        routingCollectRepository.saveAll(routingCollectList);

        // Atualizando o roteamento existente com as novas coleções
        existingRouting.getRoutingCollect().addAll(routingCollectList);
        routingRepository.save(existingRouting);

        // Retornando os detalhes atualizados do roteamento
        List<RoutingCollectDetailsFindDTO> updatedCollectDetailsList = new ArrayList<>(existingCollectDetailsList);
        updatedCollectDetailsList.addAll(newCollectDetailsList);

        RoutingAllDetailsToCreateDTO response1 = new RoutingAllDetailsToCreateDTO(
                existingRouting.getRoutingId(),
                employeeDetails,
                fleetDetails,
                existingCollectDetailsList
        );

        return response1;
    }

    public List<RoutingCollectDetailsFindDTO> getAllCollectsByRouting(UUID routingCode, String collectApiUrl) {
        // Recuperando o roteamento existente pelo código
        Optional<Routing> existingRoutingOptional = routingRepository.findByRoutingCode(routingCode);
        if (existingRoutingOptional.isEmpty()) {
            throw new IllegalArgumentException("Roteamento não encontrado.");
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
