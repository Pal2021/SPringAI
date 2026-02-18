package com.Mrpal.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class EmailAgentRequest{
       public String name;
       public String company;
       public String role;
       public String problem;
       public String email;
}
