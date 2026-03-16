package com.ansim.map.admin.writer;

import com.ansim.map.domain.entity.CctvInfrastructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CctvItemWriter implements ItemWriter<CctvInfrastructure> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends CctvInfrastructure> chunk) {
        List<? extends CctvInfrastructure> items = chunk.getItems();

        String cctvSql = """
            INSERT INTO cctv_infrastructure
              (mng_no, location, camera_count, address, management_org, created_at, updated_at)
            VALUES (?, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, ?, ?, NOW(), NOW())
            ON CONFLICT (mng_no) DO UPDATE SET
                location       = EXCLUDED.location,
                camera_count   = EXCLUDED.camera_count,
                address        = EXCLUDED.address,
                management_org = EXCLUDED.management_org,
                updated_at     = NOW()
            RETURNING id
            """;

        String safetySql = """
            INSERT INTO safety_infrastructure
              (category, weight, location, ref_id, ref_table, created_at)
            VALUES ('CCTV', 1.0, ST_SetSRID(ST_MakePoint(?, ?), 4326), ?, 'cctv_infrastructure', NOW())
            """;

        for (CctvInfrastructure item : items) {
            double lng = item.getLocation().getX(); // X = 경도
            double lat = item.getLocation().getY(); // Y = 위도

            // cctv insert → id 반환
            Long cctvId = jdbcTemplate.queryForObject(cctvSql, Long.class,
                    item.getMngNo(),
                    lng, lat,
                    item.getCameraCount(),
                    item.getAddress(),
                    item.getManagementOrg()
            );

            // safety insert
            //jdbcTemplate.update(safetySql, lng, lat, cctvId);
        }

        log.info("[CCTV Writer] {}건 처리", items.size());
    }
}