package com.mams.backend.audit;

import com.mams.backend.model.AuditLog;
import com.mams.backend.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }


    // call this from every write endpoint (purchase, transfer, assignment, expend...)
    // so we have a full trail for the "API logging" requirement
    public void log(String action, String entityName, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(currentUsername());
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
