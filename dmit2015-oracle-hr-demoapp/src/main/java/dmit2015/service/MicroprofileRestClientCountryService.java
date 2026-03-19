package dmit2015.service;

import dmit2015.dto.CountryDto;
import dmit2015.restclient.CountryMicroprofileRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMicroprofileRestClientCountryService")
@ApplicationScoped
public class MicroprofileRestClientCountryService implements CountryDtoService {

    @Inject
    @RestClient
    private CountryMicroprofileRestClient restClient;

    @Override
    public CountryDto createCountryDto(CountryDto countryDto) {
        restClient.create(countryDto);
        return countryDto;
    }

    @Override
    public Optional<CountryDto> getCountryDtoById(String id) {
        return restClient.findById(id);
    }

    @Override
    public List<CountryDto> getAllCountryDtos() {
        return restClient.findAll();
    }

    @Override
    public CountryDto updateCountryDto(CountryDto countryDto) {
        return restClient.update(countryDto.getId(), countryDto);
    }

    @Override
    public void deleteCountryDtoById(String id) {
        restClient.delete(id);
    }
}
