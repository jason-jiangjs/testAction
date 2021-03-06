package org.vog.testa.web.login;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.vog.base.model.mongo.BaseMongoMap;
import org.vog.testa.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is being un-deprecated because we want the query for the customer to happen through Hibernate instead of
 * through raw JDBC, which is the case when <sec:jdbc-user-service /> is used. We need the query to go through Hibernate
 * so that we are able to attach the necessary filters in certain circumstances.
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    // 这里必须是注册用户
    @Override
    public UserDetails loadUserByUsername(String userId) {
        return loadUserByUsernameWithChkReg(userId, true, null);
    }

    /**
     * 根据登录帐号（已注册的）查询用户
     */
    public UserDetails loadUserByUsernameWithChkReg(String userId, boolean chkReg, String fromSrc) {
        BaseMongoMap employee = userService.getUserByAccount(userId);
        if (employee == null) {
            return null;
        }

        CustomerUserDetails userObj = new CustomerUserDetails(StringUtils.trimToNull((String) employee.get("userName")), (String) employee.get("password"),
                createGrantedAuthorities(userId, employee.getIntAttribute("role")));
        userObj.setId(employee.getLongAttribute("_id"));
        userObj.setStatus(employee.getIntAttribute("status"));
        return userObj;
    }

    private List<GrantedAuthority> createGrantedAuthorities(String userId, int userRole) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (userRole == 0) {
            logger.warn("该用户的角色未设置 userid={}", userId);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_EMPTY"));
        } else if (userRole == 1) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_READONLY"));
        } else if (userRole == 2) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_WRITABLE"));
        } else if (userRole == 8) {
            grantedAuthorities.add(new SimpleGrantedAuthority("PROJ_MNG_USER"));
        } else if (userRole == 9) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN_USER"));
        } else {
            logger.warn("该用户的角色设置不正确 userid={}，role={}", userId, userRole);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_EMPTY"));
        }
        return grantedAuthorities;
    }

}
