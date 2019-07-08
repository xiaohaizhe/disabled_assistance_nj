package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DictionaryService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/4 10:52
 * @Version
 */
@Service
@Transactional
public class DictionaryService {
    private final NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final TypeOfDisabilityRepository typeOfDisabilityRepository;
    private final NursingModeRepository nursingModeRepository;
    private final DisabilityDegreeRepository disabilityDegreeRepository;

    public DictionaryService(NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository, ProjectTypeRepository projectTypeRepository, TypeOfDisabilityRepository typeOfDisabilityRepository, NursingModeRepository nursingModeRepository, DisabilityDegreeRepository disabilityDegreeRepository) {
        this.natureOfHousingPropertyRightRepository = natureOfHousingPropertyRightRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.typeOfDisabilityRepository = typeOfDisabilityRepository;
        this.nursingModeRepository = nursingModeRepository;
        this.disabilityDegreeRepository = disabilityDegreeRepository;
    }

    public JSONObject getList(String name){
        List list = new ArrayList();
        switch (name){
            case "project":
                list = projectTypeRepository.findAll();
                break;
            case "disability":
                list = typeOfDisabilityRepository.findAll();
                break;
            case "housing":
                list = natureOfHousingPropertyRightRepository.findAll();
                break;
            case "mode":
                list = nursingModeRepository.findAll();
                break;
            case "level":
                list = disabilityDegreeRepository.findAll();
                break;
                default:
                    break;
        }
        return RESCODE.SUCCESS.getJSONRES(list);
    }
}
