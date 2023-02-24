create table file_process (flow_id varchar(255) not null, file_name varchar(255) not null, last_check_date datetime not null, status varchar(255) not null, primary key (flow_id)) engine=InnoDB;
