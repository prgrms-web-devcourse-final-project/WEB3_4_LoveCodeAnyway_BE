package com.ddobang.backend.domain.party.entity;

import com.ddobang.backend.domain.member.entity.Member;
import com.ddobang.backend.domain.party.types.PartyMemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PartyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartyMemberStatus status; // HOST, APPLICANT. ACCEPTED, REJECTED, CANCELLED
}
