package com.starwars.batch.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Data
@XmlRootElement(name = "people")
public class People {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String birthYear;
    private String gender;
    private String height;
    private String mass;
    private String eyeColor;
    private String hairColor;
    private String skinColor;
}
