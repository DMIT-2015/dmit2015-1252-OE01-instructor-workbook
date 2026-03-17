package dmit2015.mapper;

import dmit2015.dto.RegionDto;
import dmit2015.entity.Region;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * This MapStruct interface contains methods on how to map a Jakarta Persistence entity to a DTO
 * (Data Transfer Object) and a method on how to map a DTO to a JPA entity.
 * <p>
 * The following code snippets shows how to call that class-level methods.
 * {@snippet :
 * //Region newRegionEntity = RegionMapper.INSTANCE.toEntity(newRegionDto);
 * //RegionDto newRegionDto = RegionMapper.INSTANCE.toDto(newRegionEntity);
 * }
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegionMapper {

    RegionMapper INSTANCE = Mappers.getMapper( RegionMapper.class );

    RegionDto toDto(Region entity);

    Region toEntity(RegionDto dto);

}