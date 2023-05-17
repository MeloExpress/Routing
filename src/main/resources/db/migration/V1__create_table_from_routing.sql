CREATE TABLE routing (
    routing_id SERIAL PRIMARY KEY,
    routing_code UUID,
    routing_employee UUID,
    routing_fleet UUID
);

CREATE TABLE routing_collect (
    routing_collect_id SERIAL PRIMARY KEY,
    collect_code UUID
);

CREATE TABLE routing_employee (
    routing_employee_id SERIAL PRIMARY KEY,
    employee_code UUID
);

CREATE TABLE routing_fleet (
    routing_fleet_id SERIAL PRIMARY KEY,
    fleet_code UUID
);