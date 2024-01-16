package com.project.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopulateTableMultiThreadRequestDTO {

    private int quantity;
    private int poolSize;

}
