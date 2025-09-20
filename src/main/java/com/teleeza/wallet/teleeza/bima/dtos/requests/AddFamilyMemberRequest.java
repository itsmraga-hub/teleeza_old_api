package com.teleeza.wallet.teleeza.bima.dtos.requests;

import com.teleeza.wallet.teleeza.bima.entities.FamilyMember;
import lombok.Data;

import java.util.List;

@Data
public class AddFamilyMemberRequest {
    private String principalPhoneNumber;
     private List<FamilyMember> familyMembers;
}
