package io.choerodon.manager

import io.choerodon.core.oauth.CustomUserDetails
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

/**
 * @author superlee
 */
@Component
class TestAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        def userDetail = new CustomUserDetails(username, 'admin', Collections.emptyList())
        userDetail.setUserId(1L)
        userDetail.setOrganizationId(1L)
        userDetail.setLanguage('zh_CN')
        userDetail.setTimeZone('CCT')
        return userDetail
    }
}
