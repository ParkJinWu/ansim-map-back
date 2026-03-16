package com.ansim.map.admin.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CctvCsvDto {

    @CsvBindByName(column = "개방자치단체코드")
    private String openLocalCode;

    @CsvBindByName(column = "관리번호")
    private String mngNo;

    @CsvBindByName(column = "관리기관명")
    private String managementOrg;

    @CsvBindByName(column = "소재지도로명주소")
    private String roadAddress;

    @CsvBindByName(column = "소재지지번주소")
    private String lotAddress;

    @CsvBindByName(column = "설치목적구분")
    private String installPurpose;

    @CsvBindByName(column = "카메라대수")
    private String cameraCount;

    @CsvBindByName(column = "카메라화소수")
    private String cameraPixels;

    @CsvBindByName(column = "촬영방면정보")
    private String shootingDirection;

    @CsvBindByName(column = "보관일수")
    private String storageDays;

    @CsvBindByName(column = "설치연월")
    private String installYearMonth;

    @CsvBindByName(column = "관리기관전화번호")
    private String managerTel;

    @CsvBindByName(column = "WGS84위도")
    private String latitude;

    @CsvBindByName(column = "WGS84경도")
    private String longitude;

    @CsvBindByName(column = "데이터기준일자")
    private String dataStandardDate;

    @CsvBindByName(column = "최종수정시점")
    private String lastModified;
}
