package io.takamaka.ERC721;

import io.takamaka.code.lang.*;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721 extends IERC721View{
    @FromContract @Payable void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte data);
    @FromContract @Payable void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId);
    @FromContract @Payable void TransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId);
    @FromContract @Payable void approve(Contract approved, UnsignedBigInteger tokenId);
    void setApprovalForAll(Contract operator, boolean approved);
    @View Contract getApproved(UnsignedBigInteger tokenId);
    @View boolean isApprovedForAll(Contract owner, Contract operator);

    class Transfer extends Event {
        public final Contract from, to;
        public final UnsignedBigInteger tokenId;

        @FromContract Transfer(Contract from, Contract to, UnsignedBigInteger tokenId){
            this.from = from;
            this.to = to;
            this. tokenId = tokenId;
        }
    }

    class Approval extends Event {
        public final Contract owner, approved;
        public final UnsignedBigInteger tokenId;

        @FromContract Approval(Contract owner, Contract approved, UnsignedBigInteger tokenId){
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
