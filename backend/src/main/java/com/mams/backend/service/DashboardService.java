package com.mams.backend.service;

import com.mams.backend.dto.DashboardResponse;
import com.mams.backend.model.User;
import com.mams.backend.model.enums.Role;
import com.mams.backend.repository.AssignmentRepository;
import com.mams.backend.repository.PurchaseRepository;
import com.mams.backend.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;

    public DashboardService(PurchaseRepository purchaseRepository, TransferRepository transferRepository,
                             AssignmentRepository assignmentRepository) {
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
    }

    // Opening balance = everything that happened strictly before "fromDate".
    // Closing balance = opening balance + this period's net movement - this period's assigned qty.
    // (An asset's quantity is already decremented from stock the moment it's assigned, not when
    // it's later marked expended - see the note in AssignmentService - so "expended" is reported
    // separately for visibility but doesn't get subtracted again here.)
    public DashboardResponse getSummary(Long baseId, Long equipmentTypeId, LocalDate fromDate, LocalDate toDate,
                                         User currentUser) {
        Long effectiveBaseId = baseId;
        if (currentUser.getRole() != Role.ADMIN) {
            effectiveBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
        }

        int openingBalance = computeOpeningBalance(effectiveBaseId, equipmentTypeId, fromDate);

        int purchases = nz(purchaseRepository.sumQuantity(effectiveBaseId, equipmentTypeId, fromDate, toDate));
        int transfersIn = nz(transferRepository.sumIncoming(effectiveBaseId, equipmentTypeId, fromDate, toDate));
        int transfersOut = nz(transferRepository.sumOutgoing(effectiveBaseId, equipmentTypeId, fromDate, toDate));
        int assigned = nz(assignmentRepository.sumAssignedQuantity(effectiveBaseId, equipmentTypeId, fromDate, toDate));
        int expended = nz(assignmentRepository.sumExpendedQuantity(effectiveBaseId, equipmentTypeId, fromDate, toDate));

        int netMovement = purchases + transfersIn - transfersOut;
        int closingBalance = openingBalance + netMovement - assigned;

        return new DashboardResponse(openingBalance, closingBalance, netMovement,
                purchases, transfersIn, transfersOut, assigned, expended);
    }

    private int computeOpeningBalance(Long baseId, Long equipmentTypeId, LocalDate fromDate) {
        if (fromDate == null) {
            // no lower bound requested, so there's nothing "before" the window
            return 0;
        }
        LocalDate dayBeforeFrom = fromDate.minusDays(1);

        int purchasesBefore = nz(purchaseRepository.sumQuantity(baseId, equipmentTypeId, null, dayBeforeFrom));
        int transfersInBefore = nz(transferRepository.sumIncoming(baseId, equipmentTypeId, null, dayBeforeFrom));
        int transfersOutBefore = nz(transferRepository.sumOutgoing(baseId, equipmentTypeId, null, dayBeforeFrom));
        int assignedBefore = nz(assignmentRepository.sumAssignedQuantity(baseId, equipmentTypeId, null, dayBeforeFrom));

        return purchasesBefore + transfersInBefore - transfersOutBefore - assignedBefore;
    }

    private int nz(Integer value) {
        return value != null ? value : 0;
    }
}
