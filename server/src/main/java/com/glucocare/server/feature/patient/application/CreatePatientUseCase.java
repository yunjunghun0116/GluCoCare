package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import com.glucocare.server.feature.patient.dto.CreatePatientRequest;
import com.glucocare.server.feature.patient.dto.CreatePatientResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePatientUseCase {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    @Value("${app.cgm-server.url-format}")
    private String cgmServerUrlFormat;

    public CreatePatientResponse execute(Long memberId, CreatePatientRequest request) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (memberPatientRelationRepository.existsByMemberAndRelationType(member, RelationType.PATIENT)) {
            throw new ApplicationException(ErrorMessage.ALREADY_EXISTS_PATIENT);
        }
        var patient = savePatientWithRequest(request);
        savePatientRelation(member, patient);
        return CreatePatientResponse.of(patient.getId(), patient.getName(), patient.getCgmServerUrl());
    }

    private Patient savePatientWithRequest(CreatePatientRequest request) {
        var patient = new Patient(request.name());
        var savedPatient = patientRepository.save(patient);

        var cgmServerUrl = String.format(cgmServerUrlFormat, savedPatient.getId());
        savedPatient.updateCgmServerUrl(cgmServerUrl);
        return savedPatient;
    }

    private void savePatientRelation(Member member, Patient patient) {
        var patientRelation = new MemberPatientRelation(member, patient, RelationType.PATIENT);
        memberPatientRelationRepository.save(patientRelation);
    }
}
