package dmit2015.service;

import dmit2015.dto.CountryDto;

import java.util.List;
import java.util.Optional;

public interface CountryDtoService {

    CountryDto createCountryDto(CountryDto countryDto);

    Optional<CountryDto> getCountryDtoById(String id);

    List<CountryDto> getAllCountryDtos();

    CountryDto updateCountryDto(CountryDto countryDto);

    void deleteCountryDtoById(String id);
}