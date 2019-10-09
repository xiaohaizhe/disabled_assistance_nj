package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.*;
import com.hd.home_disabled.entity.dictionary.DisabilityDegree;
import com.hd.home_disabled.entity.dictionary.NursingMode;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import com.hd.home_disabled.entity.dictionary.TypeOfDisability;
import com.hd.home_disabled.entity.statistic.ProjectTypeStatistic;
import com.hd.home_disabled.entity.statistic.ProjectUser;
import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import com.hd.home_disabled.entity.statistic.UserBlockStatistic;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final DingUserService dingUserService;
    private final DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository;
    private final DingUserRepository dingUserRepository;
    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    private final TypeOfDisabilityRepository typeOfDisabilityRepository;

    private final DisabilityDegreeRepository disabilityDegreeRepository;

    private final AdminRepository adminRepository;

    private final NursingModeRepository nursingModeRepository;

    private final UserblockStatisticRepository userblockStatisticRepository;

    private final ProjectUserRepository projectUserRepository;

    private final ProjectRepository projectRepository;

    private final ProjectUserDetailRepository projectUserDetailRepository;

    private final ProjectTypeStatisticRepository projectTypeStatisticRepository;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository, TypeOfDisabilityRepository typeOfDisabilityRepository, DisabilityDegreeRepository disabilityDegreeRepository, AdminRepository adminRepository, NursingModeRepository nursingModeRepository, UserblockStatisticRepository userblockStatisticRepository, ProjectUserRepository projectUserRepository, ProjectRepository projectRepository, ProjectUserDetailRepository projectUserDetailRepository, ProjectTypeStatisticRepository projectTypeStatisticRepository, DingUserRepository dingUserRepository, DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository, DingUserService dingUserService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.typeOfDisabilityRepository = typeOfDisabilityRepository;
        this.disabilityDegreeRepository = disabilityDegreeRepository;
        this.adminRepository = adminRepository;
        this.nursingModeRepository = nursingModeRepository;
        this.userblockStatisticRepository = userblockStatisticRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectRepository = projectRepository;
        this.projectUserDetailRepository = projectUserDetailRepository;
        this.projectTypeStatisticRepository = projectTypeStatisticRepository;
        this.dingUserRepository = dingUserRepository;
        this.dingUserAttendanceRecordRepository = dingUserAttendanceRecordRepository;
        this.dingUserService = dingUserService;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //model-->entity
    private User getEntity(com.hd.home_disabled.model.dto.User user) {
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
    private com.hd.home_disabled.model.dto.User getModel(User user) {
        com.hd.home_disabled.model.dto.User u = new com.hd.home_disabled.model.dto.User();
        u.setId(user.getId());
        u.setOrganizationId(user.getOrganization().getId());
        u.setName(user.getName());
        u.setIdNumber(user.getIdNumber());
        u.setDisabilityCertificateNumber(user.getDisabilityCertificateNumber());
        u.setTypeOfDisabilityId(user.getTypeOfDisability().getId());
        u.setTypeOfDisability(user.getTypeOfDisability().getName());
        u.setDisabilityDegreeId(user.getDisabilityDegree().getId());
        u.setDisabilityDegree(user.getDisabilityDegree().getType());
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
                                Optional<User> userOptional = userRepository.findByIdNumberAndStatus(user1.getIdNumber(), 1);
                                if (userOptional.isPresent()) {
                                    return RESCODE.USER_EXIST.getJSONRES();
                                }
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
                                user1.setStatus(1);
                                user1.setBlock(organization.getBlock());
                                User user2 = userRepository.saveAndFlush(user1);
                                return RESCODE.SUCCESS.getJSONRES(getModel(user2));
                            }
                            //修改用户
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

    public JSONObject getPageByOrganizationId(Integer organizationId,String userName, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<User> userPage = userRepository.findByOrganizationAndStatusAndNameLike(organizationId, 1, userName==null?"":userName,pageable);
        List<com.hd.home_disabled.model.dto.User> userList = new ArrayList<>();
        for (User user :
                userPage.getContent()) {
            com.hd.home_disabled.model.dto.User u = getModel(user);
            userList.add(u);
        }
        return RESCODE.SUCCESS.getJSONRES(userList, userPage.getTotalPages(), userPage.getTotalElements());
    }

    private List<JSONArray> getListsByOrganizationId(Integer organizationId) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<User> userList = userRepository.findByOrganizationAndStatus(organizationId, 1);
        for (User u :
                userList) {
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("name", u.getName() == null ? 0 : u.getName());
            array.add(object);
            JSONObject object1 = new JSONObject();
            object1.put("idNumber", u.getIdNumber() == null ? 0 : u.getIdNumber());
            array.add(object1);
            JSONObject object2 = new JSONObject();
            object2.put("disabilityCertificateNumber", u.getDisabilityCertificateNumber() == null ? 0 : u.getDisabilityCertificateNumber());
            array.add(object2);
            JSONObject object3 = new JSONObject();
            if (u.getTypeOfDisability() != null && u.getTypeOfDisability().getName() != null) {
                object3.put("typeOfDisability", u.getTypeOfDisability().getName());
            } else object3.put("typeOfDisability", "");
            array.add(object3);
            JSONObject object4 = new JSONObject();
            if (u.getDisabilityDegree() != null && u.getDisabilityDegree().getType() != null) {
                object4.put("disabilityDegree", u.getDisabilityDegree().getType());
            } else object4.put("disabilityDegree", "");
            array.add(object4);
            JSONObject object5 = new JSONObject();
            object5.put("address", u.getAddress() == null ? 0 : u.getAddress());
            array.add(object5);
            JSONObject object6 = new JSONObject();
            object6.put("contactNumber", u.getContactNumber() == null ? 0 : u.getContactNumber());
            array.add(object6);
            JSONObject object7 = new JSONObject();
            if (u.getNursingMode() != null && u.getNursingMode().getType() != null) {
                object7.put("nursingMode", u.getNursingMode().getType());
            } else object7.put("nursingMode", "");
            array.add(object7);
            JSONObject object8 = new JSONObject();
            object8.put("nursingMonth", u.getNursingMonth() == null ? 0 : u.getNursingMonth());
            array.add(object8);
            JSONObject object9 = new JSONObject();
            object9.put("subsidies", u.getSubsidies() == null ? 0 : u.getSubsidies());
            array.add(object9);
            JSONObject object10 = new JSONObject();
            if (u.getAdmin() != null && u.getAdmin().getName() != null)
                object10.put("adminName", u.getAdmin().getName());
            else object10.put("adminName", "");
            array.add(object10);
            JSONObject object11 = new JSONObject();
            object11.put("createTime", u.getCreateTime() == null ? "" : u.getCreateTime());
            array.add(object11);
            JSONObject object12 = new JSONObject();
            object12.put("lastModifyTime", u.getModifyTime() == null ? "" : u.getModifyTime());
            array.add(object12);
            jsonArray.add(array);
        }
        return jsonArray;
    }

    public void exportExcel(Integer organizationId, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"残疾人姓名", "身份证号", "残疾证号", "残疾类别", "残疾等级",
                "家庭住址", "联系电话", "托养方式", "托养月数", "补贴金额",
                "提交人", "提交时间", "更新时间"};
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        String fileName = "UserList";
        if (organizationOptional.isPresent()) {
            fileName += "_" + organizationOptional.get().getName();
        }
        fileName += "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByOrganizationId(organizationId), request, response);
    }

    private JSONArray getUserBlockStatistic(List<UserBlockStatistic> userBlockStatistics) {
        JSONArray array = new JSONArray();
        Long sums = 0L;
        Long visualDisabilitys = 0L;
        Long hearingDisabilitys = 0L;
        Long speechDisabilitys = 0L;
        Long physicalDisabilitys = 0L;
        Long intellectualDisabilitys = 0L;
        Long mentalDisabilitys = 0L;
        Long multipleDisabilitys = 0L;
        Long others = 0L;
        for (UserBlockStatistic userBlockStatistic : userBlockStatistics) {
            Long sum = 0L;
            Long visualDisability = 0L;
            if (userBlockStatistic.getVisualDisability() != null)
                visualDisability = userBlockStatistic.getVisualDisability();
            sum += visualDisability;
            Long hearingDisability = 0L;
            if (userBlockStatistic.getHearingDisability() != null)
                hearingDisability = userBlockStatistic.getHearingDisability();
            sum += hearingDisability;
            Long speechDisability = 0L;
            if (userBlockStatistic.getSpeechDisability() != null)
                speechDisability = userBlockStatistic.getSpeechDisability();
            sum += speechDisability;
            Long physicalDisability = 0L;
            if (userBlockStatistic.getPhysicalDisability() != null)
                physicalDisability = userBlockStatistic.getPhysicalDisability();
            sum += physicalDisability;
            Long intellectualDisability = 0L;
            if (userBlockStatistic.getIntellectualDisability() != null)
                intellectualDisability = userBlockStatistic.getIntellectualDisability();
            sum += intellectualDisability;
            Long mentalDisability = 0L;
            if (userBlockStatistic.getMentalDisability() != null)
                mentalDisability = userBlockStatistic.getMentalDisability();
            sum += mentalDisability;
            Long multipleDisability = 0L;
            if (userBlockStatistic.getMultipleDisability() != null)
                multipleDisability = userBlockStatistic.getMultipleDisability();
            sum += multipleDisability;
            Long other = 0L;
            if (userBlockStatistic.getOther() != null)
                other = userBlockStatistic.getOther();
            sum += other;
            sums += sum;
            visualDisabilitys += visualDisability;
            hearingDisabilitys += hearingDisability;
            speechDisabilitys += speechDisability;
            physicalDisabilitys += physicalDisability;
            intellectualDisabilitys += intellectualDisability;
            mentalDisabilitys += mentalDisability;
            multipleDisabilitys += multipleDisability;
            others += other;
            array.add(getJSONObject(userBlockStatistic.getBlock(), sum, visualDisability, hearingDisability, speechDisability,
                    physicalDisability, intellectualDisability, mentalDisability, multipleDisability, other));
        }
        array.add(getJSONObject("总计", sums, visualDisabilitys, hearingDisabilitys, speechDisabilitys,
                physicalDisabilitys, intellectualDisabilitys, mentalDisabilitys, multipleDisabilitys, others));
        return array;
    }

    private JSONObject getJSONObject(String block, Long sums, Long visualDisability, Long hearingDisability,
                                     Long speechDisability, Long physicalDisability, Long intellectualDisability,
                                     Long mentalDisability, Long multipleDisability, Long others) {
        JSONObject object = new JSONObject();
        object.put("block", block);
        object.put("visualDisability", visualDisability);
        object.put("hearingDisability", hearingDisability);
        object.put("speechDisability", speechDisability);
        object.put("physicalDisability", physicalDisability);
        object.put("intellectualDisability", intellectualDisability);
        object.put("mentalDisability", mentalDisability);
        object.put("multipleDisability", multipleDisability);
        object.put("other", others);
        object.put("sum", sums);
        return object;
    }

    public JSONObject getStatistic() {
        List<UserBlockStatistic> userBlockStatistics = userblockStatisticRepository.findAll();
        JSONArray array = getUserBlockStatistic(userBlockStatistics);
        return RESCODE.SUCCESS.getJSONRES(array);
    }

    private JSONArray getStatisticArray(JSONObject object) {
        JSONArray array = new JSONArray();
        JSONObject object1 = new JSONObject();
        object1.put("name", object.get("block"));
        array.add(object1);
        JSONObject object2 = new JSONObject();
        object2.put("name", object.get("visualDisability"));
        array.add(object2);
        JSONObject object3 = new JSONObject();
        object3.put("name", object.get("hearingDisability"));
        array.add(object3);
        JSONObject object4 = new JSONObject();
        object4.put("name", object.get("speechDisability"));
        array.add(object4);
        JSONObject object5 = new JSONObject();
        object5.put("name", object.get("physicalDisability"));
        array.add(object5);
        JSONObject object6 = new JSONObject();
        object6.put("name", object.get("intellectualDisability"));
        array.add(object6);
        JSONObject object7 = new JSONObject();
        object7.put("name", object.get("mentalDisability"));
        array.add(object7);
        JSONObject object8 = new JSONObject();
        object8.put("name", object.get("multipleDisability"));
        array.add(object8);
        JSONObject object9 = new JSONObject();
        object9.put("name", object.get("other"));
        array.add(object9);
        JSONObject object10 = new JSONObject();
        object10.put("name", object.get("sum"));
        array.add(object10);
        return array;
    }

    private List<JSONArray> getStatisticData() {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<UserBlockStatistic> userBlockStatistics = userblockStatisticRepository.findAll();
        JSONArray array = getUserBlockStatistic(userBlockStatistics);
        System.out.println(array);
        JSONObject object = (JSONObject) array.get(array.size() - 1);
        jsonArray.add(getStatisticArray(object));
        System.out.println(array.size());
        for (int i = 0; i < array.size() - 1; i++) {
            System.out.println(i);
            JSONObject object1 = (JSONObject) array.get(i);
            System.out.println(object1);
            jsonArray.add(getStatisticArray(object1));
        }
        System.out.println(jsonArray);
        return jsonArray;
    }

    public void getStatisticExcel(HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"街道名称", "视力残疾", "听力残疾", "言语残疾", "肢体残疾",
                "智力残疾", "精神残疾", "多重残疾", "其他", "合计"};
        String fileName = "UserListStatistic" + "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getStatisticData(), request, response);
    }

    public JSONObject statisticData() {
        //全区各类残疾人数量
        /*JSONObject object1 = new JSONObject();
        JSONObject object2 = new JSONObject();
        List<TypeOfDisability> typeOfDisabilities = typeOfDisabilityRepository.findAll();
        for (TypeOfDisability typeOfDisability : typeOfDisabilities){
            object1.put(typeOfDisability.getName(),0);
            object2.put(typeOfDisability.getName(),0);
        }

        List<User> userList = userRepository.findAll();
        for (User user:userList){
            String type = user.getTypeOfDisability().getName();
            object1.merge(type, 1, (a, b) -> (int) a + (int)b);
        }
        //全区各类残疾人参与项目情况
        List<ProjectUser> projectUserList = projectUserRepository.findAll();
        for (ProjectUser projectUser:projectUserList){
            String type = projectUser.getUser().getTypeOfDisability().getName();
            object2.merge(type, 1, (a, b) -> (int) a + (int)b);
        }
        //百分比值
        JSONObject object3 = new JSONObject();
        for (String key:object1.keySet()){
            if ((int)object2.get(key)==0){
                object3.put(key,0);
            }else {
                float value = (float) Math.round(((float) (int)object2.get(key) / (int)object1.get(key)) * 100) / 100;
                object3.put(key,value);
            }
        }
        JSONObject object = new JSONObject();
        object.put("data",object1);
        object.put("analysis",object3);*/
        JSONObject object1 = new JSONObject();
        List<TypeOfDisability> typeOfDisabilities = typeOfDisabilityRepository.findAll();
        for (TypeOfDisability typeOfDisability : typeOfDisabilities) {
            object1.put(typeOfDisability.getName(), 0);
        }

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            String type = user.getTypeOfDisability().getName();
            object1.merge(type, 1, (a, b) -> (int) a + (int) b);
        }
        JSONObject object3 = new JSONObject();
        for (String key : object1.keySet()) {
            float value;
            switch (key) {
                case "视力残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 821) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "听力残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 769) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "言语残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 49) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "肢体残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 2919) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "智力残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 648) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "精神残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 790) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "多重残疾":
                    value = (float) Math.round(((float) (int) object1.get(key) / 123) * 10000) / 10000;
                    object3.put(key, value);
                    break;
                case "其他":
                    object3.put(key, 1F);
                    break;
            }
        }
        return RESCODE.SUCCESS.getJSONRES(object3);
    }

    /**
     * 残疾人打卡项目
     *
     * @param projectId 项目id
     * @param userId    残疾人id
     * @param start     打卡开始时间
     * @param end       打卡结束时间
     * @return 结果
     */
    @Transactional
    public JSONObject clockIn(Integer projectId, Long userId, Date start, Date end, Integer adminId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*Date start;
        Date end;
        try {
            start = sdf.parse(s);
            end = sdf.parse(e);
        } catch (ParseException ex) {
            return RESCODE.TIME_PARSE_FAILURE.getJSONRES();
        }*/
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(projectId, 1);
        if (projectOptional.isPresent()) {
            Optional<Admin> adminOptional = adminRepository.findById(adminId);
            if (adminOptional.isPresent()) {
                Optional<User> userOptional = userRepository.findByStatusAndId(1, userId);
                if (userOptional.isPresent()) {
                    //project-user-detail:残疾人打卡详情记录
                    Project project = projectOptional.get();
                    User user = userOptional.get();
                    Admin admin = adminOptional.get();
                    ProjectUserDetail projectUserDetail = new ProjectUserDetail();
                    projectUserDetail.setOrganizationId(project.getOrganization().getId());
                    projectUserDetail.setProject(project);
                    projectUserDetail.setUser(user);
                    projectUserDetail.setAdmin(admin);
                    if (end.getTime() < start.getTime()) {
                        return RESCODE.CLOCK_IN_WRONG.getJSONRES();
                    }
                    projectUserDetail.setStart(start);
                    projectUserDetail.setEnd(end);
                    Float value = (float) Math.round(((end.getTime() - start.getTime()) / 1000f / 60f / 60f) * 100) / 100;
                    projectUserDetail.setLengthOfService(value);
                    projectUserDetailRepository.save(projectUserDetail);
                    //project-user:残疾人打卡总览记录
                    boolean flag = false;//记录残疾人之前是否在项目中打卡
                    List<ProjectUser> projectUsers = projectUserRepository.findByProjectAndUser(projectId, userId);
                    if (projectUsers.size() > 0) {
                        for (ProjectUser projectUser : projectUsers) {
                            flag = true;
                            projectUser.setStart(start);
                            projectUser.setEnd(end);
                            projectUser.setServicesNum(projectUser.getServicesNum() + 1);
                            projectUser.setLengthOfService(value);
                            projectUser.setTotalLengthOfService(projectUser.getTotalLengthOfService() + value);
                            projectUserRepository.saveAndFlush(projectUser);
                        }
                    } else {
                        ProjectUser projectUser = new ProjectUser();
                        projectUser.setProject(project);
                        projectUser.setUser(user);
                        projectUser.setAdmin(admin);
                        projectUser.setStart(start);
                        projectUser.setEnd(end);
                        projectUser.setServicesNum(1);
                        projectUser.setLengthOfService(value);
                        projectUser.setTotalLengthOfService(value);
                        projectUserRepository.saveAndFlush(projectUser);
                    }
                    //project-type-statistic,服务项目类型数据统计
                    Integer typeId = project.getProjectType().getId();
                    Optional<ProjectTypeStatistic> projectTypeStatisticOptional = projectTypeStatisticRepository.findByProjectType(typeId);
                    if (projectTypeStatisticOptional.isPresent()) {
                        ProjectTypeStatistic projectTypeStatistic = projectTypeStatisticOptional.get();
                        if (!flag) {
                            projectTypeStatistic.setPersonCountSum(projectTypeStatistic.getPersonCountSum() + 1);
                        }
                        projectTypeStatistic.setPersonTimeSum(projectTypeStatistic.getPersonTimeSum() + 1);
                        projectTypeStatistic.setTotalTimeSum(projectTypeStatistic.getTotalTimeSum() + value);
                        float averageTime = (float) Math.round((projectTypeStatistic.getTotalTimeSum() / projectTypeStatistic.getPersonTimeSum()) * 100) / 100;
                        projectTypeStatistic.setAverageTime(averageTime);
                        projectTypeStatisticRepository.saveAndFlush(projectTypeStatistic);
                    } else {
                        ProjectTypeStatistic projectTypeStatistic = new ProjectTypeStatistic();
                        ProjectType projectType = new ProjectType();
                        projectType.setId(typeId);
                        projectTypeStatistic.setProjectType(projectType);
                        projectTypeStatistic.setAverageTime(value);
                        projectTypeStatistic.setTotalTimeSum(value);
                        projectTypeStatistic.setPersonCountSum(1L);
                        projectTypeStatistic.setPersonTimeSum(1L);
                        projectTypeStatisticRepository.saveAndFlush(projectTypeStatistic);
                    }
                    //project数据统计
                    if (project.getPersonTimeSum() != null)
                        project.setPersonTimeSum(project.getPersonTimeSum() + 1);
                    else project.setPersonTimeSum(1);
                    if (!flag) {
                        if (project.getPersonCountSum() != null)
                            project.setPersonCountSum(project.getPersonCountSum() + 1);
                        else project.setPersonCountSum(1);
                    }
                    if (project.getTotalTimeSum() != null)
                        project.setTotalTimeSum(project.getTotalTimeSum() + value);
                    else project.setTotalTimeSum(value);
                    float averageTime_project = (float) Math.round((project.getTotalTimeSum() / project.getPersonTimeSum()) * 100) / 100;
                    project.setAverageTime(averageTime_project);
                    projectRepository.saveAndFlush(project);
                    //organization数据统计
                    Organization organization = project.getOrganization();
                    if (!flag) {
                        if (organization.getPersonCountSum() != null)
                            organization.setPersonCountSum(organization.getPersonCountSum() + 1);
                        else organization.setPersonCountSum(1);
                    }
                    if (organization.getPersonTimeSum() != null)
                        organization.setPersonTimeSum(organization.getPersonTimeSum() + 1);
                    else organization.setPersonTimeSum(1);
                    if (organization.getTotalTimeSum() != null)
                        organization.setTotalTimeSum(organization.getTotalTimeSum() + value);
                    else organization.setTotalTimeSum(value);
                    float averageTime_organization = (float) Math.round((organization.getTotalTimeSum() / organization.getPersonTimeSum()) * 100) / 100;
                    organization.setAverageTime(averageTime_organization);
                    organizationRepository.saveAndFlush(organization);
                    return RESCODE.SUCCESS.getJSONRES();
                }
                return RESCODE.USER_ID_NOT_EXIST.getJSONRES();
            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES();
    }

    private JSONObject getUserDetail(DingUserAttendanceRecord record){
        User user = dingUserService.getUserId(record.getDingUserId());
        JSONObject object = new JSONObject();
        object.put("id", record.getId());
        object.put("name", user.getName());
        object.put("organizationName",user.getOrganization().getName());
        object.put("disabilityCertificateNumber", user.getDisabilityCertificateNumber());
        if (record.getProjectId()!= null && projectRepository.findById(record.getProjectId()).isPresent()){
            object.put("projectName",projectRepository.findById(record.getProjectId()).get().getName());
        }else{
            object.put("projectName",null);
        }
        object.put("userCheckTime",record.getUserCheckTime());
        return object;
    }

    public JSONObject userClockInRecord(Integer organizationId,Integer hasProject, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        List<User> userList = userRepository.findByOrganizationAndStatus(organizationId, 1);
        List<String> userMobiles = new ArrayList<>();
        for (User user :
                userList) {
            userMobiles.add(user.getContactNumber());
        }
        List<String> dingUserList = dingUserRepository.findByMobile(userMobiles);
        Page<DingUserAttendanceRecord> dingUserAttendanceRecordPage = null;
        switch (hasProject){
            case 1:     //全部
                dingUserAttendanceRecordPage = dingUserAttendanceRecordRepository
                        .findByDingUserIdIn(dingUserList, pageable);
                break;
            case 0:     //未选择项目
                dingUserAttendanceRecordPage = dingUserAttendanceRecordRepository
                        .findByDingUserIdInAndProjectId(dingUserList,pageable);

        }
        List<JSONObject> records = new ArrayList<>();
        for (DingUserAttendanceRecord record:
                dingUserAttendanceRecordPage.getContent()) {
            records.add(getUserDetail(record));
        }
        return RESCODE.SUCCESS.getJSONRES(records,
                dingUserAttendanceRecordPage.getTotalPages(),dingUserAttendanceRecordPage.getTotalElements());
    }
}
