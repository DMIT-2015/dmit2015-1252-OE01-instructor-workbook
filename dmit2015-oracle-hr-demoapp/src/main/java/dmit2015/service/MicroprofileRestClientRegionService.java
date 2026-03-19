package dmit2015.service;

import dmit2015.dto.RegionDto;
import dmit2015.restclient.RegionMicroprofileRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMicroprofileRestClientRegionService")
@ApplicationScoped
public class MicroprofileRestClientRegionService implements RegionDtoService{

    @Inject
    @RestClient
    private RegionMicroprofileRestClient restClient;

    @Override
    public RegionDto createRegionDto(RegionDto regionDto) {
        restClient.create(regionDto);
        return regionDto;
    }

    @Override
    public Optional<RegionDto> getRegionDtoById(Long id) {
        return restClient.findById(id);
    }

    @Override
    public List<RegionDto> getAllRegionDtos() {
        return restClient.findAll();
    }

    @Override
    public RegionDto updateRegionDto(RegionDto regionDto) {
        return restClient.update(regionDto.getId(), regionDto);
    }

    @Override
    public void deleteRegionDtoById(Long id) {
        restClient.delete(id);
    }
}
