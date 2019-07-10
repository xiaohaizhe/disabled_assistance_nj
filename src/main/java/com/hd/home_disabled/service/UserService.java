package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.entity.dictionary.DisabilityDegree;
import com.hd.home_disabled.entity.dictionary.NursingMode;
import com.hd.home_disabled.entity.dictionary.TypeOfDisability;
import com.hd.home_disabled.entity.statistic.UserBlockStatistic;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private final AdminRepository adminRepository;

    private final NursingModeRepository nursingModeRepository;

    private final UserblockStatisticRepository userblockStatisticRepository;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository, TypeOfDisabilityRepository typeOfDisabilityRepository, DisabilityDegreeRepository disabilityDegreeRepository, AdminRepository adminRepository, NursingModeRepository nursingModeRepository, UserblockStatisticRepository userblockStatisticRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.typeOfDisabilityRepository = typeOfDisabilityRepository;
        this.disabilityDegreeRepository = disabilityDegreeRepository;
        this.adminRepository = adminRepository;
        this.nursingModeRepository = nursingModeRepository;
        this.userblockStatisticRepository = userblockStatisticRepository;
    }

    //model-->entity
    User getEntity(com.hd.home_disabled.model.dto.User user) {
        User u = new User();
        if (user.getId() != null) u.setId(user.getId());
        Optional<Organization> organizationOptional = organizationRepository.findById(user.getOrganizationId());
        Organization organization;
        if (organizationOptional.isPresent()) {
            organization = organizationOptional.get();
            organization.setId(user.getOrganizationId());
            u.setOrganization(organization);
            u.setBlock(organization.getBlock());
        }

        u.setName(user.getName());
        u.setIdNumber(user.getIdNumber());
        u.setDisabilityCertificateNumber(user.getDisabilityCertificateNumber());

        TypeOfDisability typeOfDisability = new TypeOfDisability();
        typeOfDisability.setId(user.getTypeOfDisabilityId());
        u.setTypeOfDisability(typeOfDisability);
        DisabilityDegree disabilityDegree = new DisabilityDegree();
        disabilityDegree.setId(user.getDisabilityDegreeId());
        u.setDisabilityDegree(disabilityDegree);

        u.setAddress(user.getAddress());
        u.setContactNumber(user.getContactNumber());

        NursingMode nursingMode = new NursingMode();
        nursingMode.setId(user.getNursingModeId());
        u.setNursingMode(nursingMode);

        u.setNursingMonth(user.getNursingMonth());
        u.setSubsidies(user.getSubsidies());

        Admin admin = new Admin();
        admin.setId(user.getAdminId());
        u.setAdmin(admin);

        return u;
    }

    //entity-->model
    com.hd.home_disabled.model.dto.User getModel(User user) {
        com.hd.home_disabled.model.dto.User u = new com.hd.home_disabled.model.dto.User();
        u.setId(user.getId());
        u.setOrganizationId(user.getOrganization().getId());
        u.setName(user.getName());
        u.setIdNumber(user.getIdNumber());
        u.setDisabilityCertificateNumber(user.getDisabilityCertificateNumber());
        u.setTypeOfDisabilityId(user.getTypeOfDisability().getId());
        u.setDisabilityDegreeId(user.getDisabilityDegree().getId());
        u.setAddress(user.getAddress());
        u.setContactNumber(user.getContactNumber());
        u.setNursingModeId(user.getNursingMode().getId());
        u.setNursingMonth(user.getNursingMonth());
        u.setSubsidies(user.getSubsidies());
        u.setAdminName(user.getAdmin().getName());
        u.setCreateTime(user.getCreateTime());
        u.setLastModifyTime(user.getModifyTime());
        return u;
    }

    /**
     * 新建或修改残疾人信息
     * 修改带user-id
     *
     * @param user 残疾人model
     * @return 结果
     */
    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.User user) {
        User user1 = getEntity(user);
        if (user1.getAdmin() != null &&
                user1.getAdmin().getId() != null &&
                adminRepository.existsById(user1.getAdmin().getId())) {
            if (user1.getOrganization() != null &&
                    user1.getOrganization().getId() != null &&
                    organizationRepository.existsById(user1.getOrganization().getId())) {
                Organization organization = organizationRepository.getOne(user1.getOrganization().getId());
                if (user1.getDisabilityDegree() != null &&
                        user1.getDisabilityDegree().getId() != null &&
                        disabilityDegreeRepository.existsById(user1.getDisabilityDegree().getId())) {
                    if (user1.getTypeOfDisability() != null &&
                            user1.getTypeOfDisability().getId() != null &&
                            typeOfDisabilityRepository.existsById(user1.getTypeOfDisability().getId())) {
                        if (user1.getNursingMode() != null &&
                                user1.getNursingMode().getId() != null &&
                                nursingModeRepository.existsById(user1.getNursingMode().getId())) {
                            if (user1.getId() == null) {
                                //user模型不带id，表示新增
                                UserBlockStatistic userBlockStatistic = null;
                                Optional<UserBlockStatistic> userBlockStatisticOptional = userblockStatisticRepository.findByBlockLike(user1.getBlock());
                                if (userBlockStatisticOptional.isPresent()) {
                                    userBlockStatistic = userBlockStatisticOptional.get();
                                }
                                //残疾人街道统计数据增加
                                switch (user.getTypeOfDisabilityId()) {
                                    case 1://视力残疾
                                        if (userBlockStatistic != null) {
                                            //街道数据存在，更新街道数据
                                            if (userBlockStatistic.getVisualDisability() != null) {
                                                userBlockStatistic.setVisualDisability(userBlockStatistic.getVisualDisability() + 1);
                                            } else {
                                                userBlockStatistic.setVisualDisability(1L);
                                            }
                                        } else {
                                            //残疾人街道数据不存在，新建街道数据
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setVisualDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 2://听力残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getHearingDisability() != null) {
                                                userBlockStatistic.setHearingDisability(userBlockStatistic.getHearingDisability() + 1);
                                            } else {
                                                userBlockStatistic.setHearingDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setHearingDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 3://言语残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getSpeechDisability() != null) {
                                                userBlockStatistic.setSpeechDisability(userBlockStatistic.getSpeechDisability() + 1);
                                            } else {
                                                userBlockStatistic.setSpeechDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setSpeechDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 4://肢体残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getPhysicalDisability() != null) {
                                                userBlockStatistic.setPhysicalDisability(userBlockStatistic.getPhysicalDisability() + 1);
                                            } else {
                                                userBlockStatistic.setPhysicalDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setPhysicalDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 5://智力残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getIntellectualDisability() != null) {
                                                userBlockStatistic.setIntellectualDisability(userBlockStatistic.getIntellectualDisability() + 1);
                                            } else {
                                                userBlockStatistic.setIntellectualDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setIntellectualDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 6://精神残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getMentalDisability() != null) {
                                                userBlockStatistic.setMentalDisability(userBlockStatistic.getMentalDisability() + 1);
                                            } else {
                                                userBlockStatistic.setMentalDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setMentalDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    case 7://多重残疾
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getMultipleDisability() != null) {
                                                userBlockStatistic.setMultipleDisability(userBlockStatistic.getMultipleDisability() + 1);
                                            } else {
                                                userBlockStatistic.setMultipleDisability(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setMultipleDisability(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                    default:
                                        if (userBlockStatistic != null) {
                                            if (userBlockStatistic.getOther() != null) {
                                                userBlockStatistic.setOther(userBlockStatistic.getOther() + 1);
                                            } else {
                                                userBlockStatistic.setOther(1L);
                                            }
                                        } else {
                                            UserBlockStatistic userBlockStatistic1 = new UserBlockStatistic(user1.getBlock());
                                            userBlockStatistic1.setOther(1L);
                                            userblockStatisticRepository.save(userBlockStatistic1);
                                        }
                                        break;
                                }
                            }
                            user1.setStatus(1);
                            user1.setBlock(organization.getBlock());
                            User user2 = userRepository.saveAndFlush(user1);
                            return RESCODE.SUCCESS.getJSONRES(getModel(user2));
                        }
                        return RESCODE.NURSING_MODE_NOT_EXIST.getJSONRES();
                    }
                    return RESCODE.TYPE_OF_DISABILITY_NOT_EXIST.getJSONRES();
                }
                return RESCODE.DISABILITY_DEGREE_NOT_EXIST.getJSONRES();
            }
            return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }


    /**
     * 根据id删除残疾人信息
     *
     * @param id 用户唯一标识
     * @return 结果
     */
    public JSONObject delete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userOptional.get().setStatus(0);
            //残疾人街道统计数据
            UserBlockStatistic userBlockStatistic = null;
            Optional<UserBlockStatistic> userBlockStatisticOptional = userblockStatisticRepository.findByBlockLike(userOptional.get().getBlock());
            if (userBlockStatisticOptional.isPresent()) {
                userBlockStatistic = userBlockStatisticOptional.get();
            }
            switch (userOptional.get().getTypeOfDisability().getId()) {
                case 1://视力残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getVisualDisability() != null &&
                            userBlockStatistic.getVisualDisability() > 0) {
                        userBlockStatistic.setVisualDisability(userBlockStatistic.getVisualDisability() - 1);
                    }
                    break;
                case 2://听力残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getHearingDisability() != null &&
                            userBlockStatistic.getHearingDisability() > 0) {
                        userBlockStatistic.setHearingDisability(userBlockStatistic.getHearingDisability() - 1);
                    }
                    break;
                case 3://言语残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getSpeechDisability() != null &&
                            userBlockStatistic.getSpeechDisability() > 0) {
                        userBlockStatistic.setSpeechDisability(userBlockStatistic.getSpeechDisability() - 1);
                    }
                    break;
                case 4://肢体残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getPhysicalDisability() != null &&
                            userBlockStatistic.getPhysicalDisability() > 0) {
                        userBlockStatistic.setPhysicalDisability(userBlockStatistic.getPhysicalDisability() - 1);
                    }
                    break;
                case 5://智力残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getIntellectualDisability() != null &&
                            userBlockStatistic.getIntellectualDisability() > 0) {
                        userBlockStatistic.setIntellectualDisability(userBlockStatistic.getIntellectualDisability() - 1);
                    }
                    break;
                case 6://精神残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getMentalDisability() != null &&
                            userBlockStatistic.getMentalDisability() > 0) {
                        userBlockStatistic.setMentalDisability(userBlockStatistic.getMentalDisability() - 1);
                    }
                    break;
                case 7://多重残疾
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getMultipleDisability() != null &&
                            userBlockStatistic.getMultipleDisability() > 0) {
                        userBlockStatistic.setMultipleDisability(userBlockStatistic.getMultipleDisability() - 1);
                    }
                    break;
                default:
                    if (userBlockStatistic != null &&
                            userBlockStatistic.getOther() != null &&
                            userBlockStatistic.getOther() > 0) {
                        userBlockStatistic.setOther(userBlockStatistic.getOther() - 1);
                    }
                    break;
            }
            return RESCODE.SUCCESS.getJSONRES();
        }
        return RESCODE.USER_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 根据id查询详情
     *
     * @param id 用户id
     * @return 单个用户详情
     */
    public JSONObject getById(Long id) {
        Optional<User> userOptional = userRepository.findByStatusAndId(1, id);
        if (userOptional.isPresent()) {
            com.hd.home_disabled.model.dto.User user = getModel(userOptional.get());
            return RESCODE.SUCCESS.getJSONRES(user);
        }
        return RESCODE.USER_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<User> userPage = userRepository.findByOrganizationAndStatus(organizationId, 1, pageable);
        List<com.hd.home_disabled.model.dto.User> userList = new ArrayList<>();
        for (User user :
                userPage.getContent()) {
            com.hd.home_disabled.model.dto.User u = getModel(user);
            userList.add(u);
        }
        return RESCODE.SUCCESS.getJSONRES(userList, userPage.getTotalPages(), userPage.getTotalElements());
    }


}
