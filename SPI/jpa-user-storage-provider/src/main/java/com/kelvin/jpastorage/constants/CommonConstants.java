package com.kelvin.jpastorage.constants;

import com.kelvin.jpastorage.entity.UserEntity;

import java.util.HashSet;
import java.util.Set;

public class CommonConstants {
    public static final Set<String> USER_ATTRIBUTE_SET = new HashSet<>();

    static {
        USER_ATTRIBUTE_SET.add(UserEntity.PHONE);
        USER_ATTRIBUTE_SET.add(UserEntity.EMAIL);
        USER_ATTRIBUTE_SET.add(UserEntity.FIRST_NAME);
        USER_ATTRIBUTE_SET.add(UserEntity.LAST_NAME);
    }
}
