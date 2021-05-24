package io.takamaka.ERC721;

import io.takamaka.code.lang.*;

import java.math.BigInteger;

public interface IERC721 extends IERC721View{
    @FromContract @Payable void safeTransferFrom(BigInteger tokenId, Contract from, Contract to, byte data);
    @FromContract @Payable void safeTransferFrom(BigInteger tokenId, Contract from, Contract to);
    @FromContract @Payable void TransferFrom(BigInteger tokenId, Contract from, Contract to);
    @FromContract @Payable void approve(BigInteger tokenId, Contract approved);
    @FromContract void setApprovalForAll(Contract operator, boolean approved);
    @View Contract getApproved(BigInteger tokenId);
    @View boolean isApprovedForAll(Contract owner, Contract operator);

    class Transfer extends Event {
        public final Contract from, to;
        public final BigInteger tokenId;

        @FromContract Transfer(Contract from, Contract to, BigInteger tokenId){
            this.from = from;
            this.to = to;
            this. tokenId = tokenId;
        }
    }

    class Approval extends Event {
        public final Contract owner, approved;
        public final BigInteger tokenId;

        @FromContract Approval(Contract owner, Contract approved, BigInteger tokenId){
            this.owner = owner;
            this.approved = approved;
            this.tokenId = tokenId;
        }
    }

    class ApprovalForAll extends Event {
        public final Contract owner, operator;
        public final boolean approved;

        @FromContract ApprovalForAll(Contract owner, Contract operator, boolean approved){
            this.owner = owner;
            this.operator = operator;
            this.approved = approved;
        }
    }
}
