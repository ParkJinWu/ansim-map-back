package com.ansim.map.admin.processor;

import com.ansim.map.admin.dto.CctvCsvDto;
import com.ansim.map.domain.entity.CctvInfrastructure;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CctvItemProcessor implements ItemProcessor<CctvCsvDto, CctvInfrastructure> {

    private static final GeometryFactory GEO_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public CctvInfrastructure process(CctvCsvDto dto) {

        // 관리번호 없으면 skip
        if (dto.getMngNo() == null || dto.getMngNo().isBlank()) {
            log.warn("[CCTV Processor] 관리번호 없음, skip");
            return null;
        }

        // 위경도 없으면 skip (location은 필수값)
        Double lat = parseDouble(dto.getLatitude());
        Double lng = parseDouble(dto.getLongitude());
        if (lat == null || lng == null) {
            log.warn("[CCTV Processor] 위경도 없음, skip: mngNo={}", dto.getMngNo());
            return null;
        }

        // 주소는 도로명 우선, 없으면 지번주소
        String address = (dto.getRoadAddress() != null && !dto.getRoadAddress().isBlank())
                ? dto.getRoadAddress()
                : dto.getLotAddress();

        // Point 생성 (X=경도, Y=위도 순서 주의)
        Point location = GEO_FACTORY.createPoint(new Coordinate(lng, lat));

        return CctvInfrastructure.builder()
                .mngNo(dto.getMngNo().trim())
                .location(location)
                .cameraCount(parseInt(dto.getCameraCount()))
                .address(address)
                .managementOrg(dto.getManagementOrg())
                .build();
    }

    private Double parseDouble(String val) {
        try {
            return (val != null && !val.isBlank()) ? Double.parseDouble(val.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String val) {
        try {
            return (val != null && !val.isBlank()) ? Integer.parseInt(val.trim()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
