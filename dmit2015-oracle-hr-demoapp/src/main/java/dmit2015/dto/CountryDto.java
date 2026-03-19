package dmit2015.dto;

import lombok.Data;

@Data
public class CountryDto {

    private String id;
    private String countryName;
    private Long regionId;
    private String regionName;

}
