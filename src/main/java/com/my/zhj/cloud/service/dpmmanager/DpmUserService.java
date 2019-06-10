//package com.my.zhj.cloud.service.dpmmanager;
//
//
//import com.my.zhj.cloud.springjpa.entity.DpmUser;
//import com.my.zhj.cloud.springjpa.repository.DpmUserRepository;
//import com.querydsl.core.BooleanBuilder;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//@Slf4j
//@Service
//public class DpmUserService {
//    @Autowired
//    private DpmUserRepository dpmUserRepository;
//
//    @Value("${oauth.jwt.secret}")
//    private String secret;
//    @Value("${oauth.jwt.expired}")
//    private Long expired;
//
//
//    public List<DpmUser> checkType(){
//        return dpmUserRepository.findAll();
//    }
//
//    public JwtTokenRsp login(UserInfo userInfo) {
//        Iterator<User> iterator = userRepository.findAll(QUser.user.username.eq(userInfo.getUsername())).iterator();
//        if (!iterator.hasNext()) {
//            throw new BatchPasswordErrorRequestException("用户不存在");
//        }
//        User userPO = iterator.next();
//        if (!StringUtils.equalsIgnoreCase(userPO.getPassword(), BaseSecurityUtils.md5(userInfo.getPassword()))) {
//            throw new BatchPasswordErrorRequestException("密码不正确");
//        }
//        AuthInfoDto authInfo = new AuthInfoDto();
//
//        authInfo.setUserName(userPO.getUsername());
//        authInfo.setLev(userPO.getLev());
//        authInfo.setUserType(userPO.getUserType());
//        authInfo.setExpired(System.currentTimeMillis() + expired * 3600 * 1000);
//
//        JwtToken token = BaseJwtUtils.getToken(BaseJsonUtils.writeValue(authInfo), expired * 3600 * 1000, secret);
//        JwtTokenRsp rsp = BaseBeanUtils.convert(token, JwtTokenRsp.class);
//        rsp.setUserName(userPO.getUsername());
//        return rsp;
//    }
//
//    public void checkRole() {
//        String lev = MDC.get(MDCKey.LEV);
//        if (UserLevEnum.admin.getCode() != Integer.valueOf(lev) && UserLevEnum.update_del.getCode() != Integer.valueOf(lev)) {
//            throw new BatchNotAdminException("用户操作权限不够--->" + MDC.get(MDCKey.USER));
//        }
//
//    }
//
//    public void createUser(UserInfoReq req) {
//        String lev = MDC.get(MDCKey.LEV);
//        if (UserLevEnum.admin.getCode() != Integer.valueOf(lev)){
//            throw new BatchNotAdminException("用户操作权限不够--->" + MDC.get(MDCKey.USER));
//        }
//        Iterator<User> iterator = userRepository.findAll(QUser.user.username.eq(req.getUsername())).iterator();
//        if (iterator.hasNext()) {
//            throw new BatchNotFoundException("用户已存在");
//        }
//        User user = new User();
//        user.setLev(req.getLev());
//        user.setPassword(BaseSecurityUtils.md5(req.getPassword()));
//        user.setUserType(req.getType());
//        user.setUsername(req.getUsername());
//        user.setCreateAt(new Date());
//        user.setCreateBy(MDC.get(MDCKey.USER));
//        userRepository.save(user);
//    }
//
//    public void updateUser(UserInfoReq req) {
//        String lev = MDC.get(MDCKey.LEV);
//        if (UserLevEnum.admin.getCode() != Integer.valueOf(lev)){
//            throw new BatchNotAdminException("用户操作权限不够--->" + MDC.get(MDCKey.USER));
//        }
//        Iterator<User> iterator = userRepository.findAll(QUser.user.username.eq(req.getUsername()).and(QUser.user.id.ne(req.getId()))).iterator();
//        if (iterator.hasNext()) {
//            throw new BatchNotFoundException("用户名已存在,请更换用户名");
//        }
//        User user = userRepository.findOne(req.getId());
//        if(StringUtils.isEmpty(req.getPassword())){
//            req.setPassword(null);
//        }
//        BaseBeanUtils.copyNoneNullProperties(req,user);
//        if(StringUtils.isNotBlank(req.getType())){
//            user.setUserType(req.getType());
//        }
//        if(StringUtils.isNotEmpty(req.getPassword())) {
//            user.setPassword(BaseSecurityUtils.md5(req.getPassword()));
//        }
//        user.setLastUpdateAt(new Date());
//        user.setLastUpdateBy(MDC.get(MDCKey.USER));
//        userRepository.save(user);
//    }
//
//    public BatchResponse<BatchPageResponse<UserInfoReq>> searchUser(String userName,String type,Integer userLevel, Integer page, Integer size) {
//        String lev = MDC.get(MDCKey.LEV);
//        if (UserLevEnum.admin.getCode() != Integer.valueOf(lev)){
//            throw new BatchNotAdminException("用户操作权限不够--->" + MDC.get(MDCKey.USER));
//        }
//        BooleanBuilder booleanBuilder = new BooleanBuilder();
//        if (StringUtils.isNotBlank(userName)) {
//            booleanBuilder.and(QUser.user.username.containsIgnoreCase(userName));
//        }
//        if (StringUtils.isNotEmpty(type)) {
//            booleanBuilder.and(QUser.user.userType.contains(type));
//        }
//        if (null != userLevel) {
//            booleanBuilder.and(QUser.user.lev.eq(userLevel));
//        }
//        Pageable pageable = ConvertUtils.pageRequest(page, size);
//        Page<User> result = userRepository.findAll(booleanBuilder, pageable);
//
//        return ConvertUtils.toPageRsp(result, ConvertUtils::toUserInfo);
//    }
//
//}
