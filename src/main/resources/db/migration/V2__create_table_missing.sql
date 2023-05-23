CREATE TABLE public.routing_routing_collect (
    routing_routing_id INT NOT NULL,
    routing_collect_routing_collect_id INT NOT NULL,
    CONSTRAINT fk_routing_routing_collect FOREIGN KEY (routing_routing_id) REFERENCES routing (routing_id),
    CONSTRAINT fk_routing_collect_routing_collect FOREIGN KEY (routing_collect_routing_collect_id) REFERENCES routing_collect (routing_collect_id)
);
