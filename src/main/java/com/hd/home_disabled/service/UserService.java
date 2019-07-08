package com.hd.home_disabled.service;

import com.hd.home_disabled.repository.DisabilityDegreeRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.repository.TypeOfDisabilityRepository;
import com.hd.home_disabled.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:01
 * @Version
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    private final TypeOfDisabilityRepository typeOfDisabilityRepository;

    private final DisabilityDegreeRepository disabilityDegreeRepository;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository, TypeOfDisabilityRepository typeOfDisabilityRepository, DisabilityDegreeRepository disabilityDegreeRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.typeOfDisabilityRepository = typeOfDisabilityRepository;
        this.disabilityDegreeRepository = disabilityDegreeRepository;
    }

//    public JSONObject saveAndFlush(User user) {
//        if (user.getOrganization() != null
//                && user.getOrganization().getId() != null
//                && organizationRepository.existsById(user.getOrganization().getId())
//                && user.getTypeOfDisability() != null
//                && user.getTypeOfDisability().getId() != null
//                && typeOfDisabilityRepository.existsById(user.getTypeOfDisability().getId())
//                && user.getDisabilityDegree() != null
//                && user.getDisabilityDegree().getId() != null
//                && disabilityDegreeRepository.existsById(user.getDisabilityDegree().getId())) {
//            user.setStatus(1);
//            User user1 = userRepository.saveAndFlush(user);
//            return RESCODE.SUCCESS.getJSONRES(user1);
//        }
//        return RESCODE.FAILURE.getJSONRES();
//    }


}
