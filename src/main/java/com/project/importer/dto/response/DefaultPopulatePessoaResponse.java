package com.project.importer.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultPopulatePessoaResponse {

    private long totalRegistered;
    private String loadTime;

}
