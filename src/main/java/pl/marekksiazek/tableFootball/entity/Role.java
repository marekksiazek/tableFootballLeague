package pl.marekksiazek.tableFootball.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "XXXXX")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name")
    private String role_name;



}
