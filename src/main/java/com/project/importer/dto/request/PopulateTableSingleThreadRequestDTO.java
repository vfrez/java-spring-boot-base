package com.project.importer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopulateTableSingleThreadRequestDTO {

    private int quantity;
    private int batchSize;
    private int poolSize;

}
