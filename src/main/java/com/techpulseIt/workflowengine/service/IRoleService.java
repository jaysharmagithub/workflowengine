package com.techpulseIt.workflowengine.service;

import com.techpulseIt.workflowengine.entity.Role;
import com.techpulseIt.workflowengine.entity.User;
import com.techpulseIt.workflowengine.exception.RoleAlreadyExistException;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();
    Role createRole(Role theRole) throws RoleAlreadyExistException;

    void   deleteRole(Long id);
    Role findByName(String name);

    User removeUserFromRole(Long userId, Long roleId);

    User assignRoleToUser(Long userId, Long roleId);


    Role removeAllUsersFromRole(Long roleId);

}
