package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.Payable;
import io.takamaka.code.lang.View;
import io.takamaka.code.util.StorageMap;
import io.takamaka.code.util.StorageTreeMap;

import java.math.BigInteger;

import static io.takamaka.code.lang.Takamaka.event;
import static io.takamaka.code.lang.Takamaka.require;

public class ERC721 extends Contract implements IERC721{
    private final StorageMap<BigInteger, Contract> idToOwner = new StorageTreeMap<>();
    private final StorageMap<BigInteger, Contract> idToApproval = new StorageTreeMap<>();
    private final StorageMap<Contract, BigInteger> ownerToNFTokenCount = new StorageTreeMap<>();
    private final StorageMap<Contract, StorageMap<Contract, Boolean>> ownerToOperators = new StorageTreeMap<>();
    private final String name;
    private final String symbol;

    public ERC721(String name, String symbol){
        this.name = name;
        this.symbol = symbol;
    }

    public final BigInteger ZERO = new BigInteger("0");
    public final BigInteger ONE = new BigInteger("1");

    @Override
    public @View BigInteger balanceOf(Contract owner) {
        return ownerToNFTokenCount.getOrDefault(owner, ZERO);
    }

    @Override
    public @View Contract ownerOf(BigInteger tokenId){ return idToOwner.get(tokenId); }

    @Override
    public @FromContract @Payable void approve(BigInteger tokenId, Contract approved) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != approved, "ERC721: Approved is owner");
        require(tokenOwner != null, "ERC721: Not valid NFT");
        canOperate(tokenId);

        idToApproval.putIfAbsent(tokenId, approved);
        event(new Approval(tokenOwner, approved, tokenId));
    }

    @Override
    public @View Contract getApproved(BigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != null, "ERC721: Not valid NFT");
        return idToApproval.get(tokenId);
    }

    @Override
    public @FromContract void setApprovalForAll(Contract operator, boolean approved) {
        StorageMap<Contract, Boolean> innerMap = ownerToOperators.get(caller());
        if(innerMap == null) {
            ownerToOperators.put(caller(), innerMap = new StorageTreeMap<>());
        }
        innerMap.put(operator, approved);

        event(new ApprovalForAll(caller(), operator, approved));
    }

    @Override
    public @View boolean isApprovedForAll(Contract owner, Contract operator) {
        return ownerToOperators.get(owner) != null
                && ownerToOperators.get(owner).getOrDefault(operator, false);
    }

    @Override
    public @FromContract @Payable void safeTransferFrom(BigInteger tokenId, Contract from, Contract to) {
        _safeTransferFrom(from, to, tokenId, (byte) 0);
    }

    @Override
    public @FromContract @Payable void safeTransferFrom(BigInteger tokenId, Contract from, Contract to, byte data) {
        _safeTransferFrom(from, to, tokenId, data);
    }

    @Override
    public @FromContract @Payable void TransferFrom(BigInteger tokenId, Contract from, Contract to) {
        transfer(from, to, tokenId);
    }

    protected @FromContract void _safeTransferFrom(Contract from, Contract to, BigInteger tokenId, byte data){
        transfer(from, to, tokenId);
        //TODO: ERC721Receiver if "to" is a smart  contract
    }

    protected @FromContract void transfer(Contract from, Contract to, BigInteger tokenId){
        require(from != null, "ERC721: Transfer from null account");
        require(to != null, "ERC721: Transfer to null account");
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != null, "ERC721: Not valid NFT");
        require(tokenOwner == from, "ERC721: Not owner");

        canTransfer(tokenId);
        clearApproval(tokenId);
        removeNFToken(from, tokenId);
        addNFToken(to, tokenId);

        event(new Transfer(from, to, tokenId));
    }

    protected void removeNFToken(Contract from, BigInteger tokenId) {
        idToOwner.remove(tokenId);

        BigInteger value = ownerToNFTokenCount.get(from);
        ownerToNFTokenCount.remove(from);
        ownerToNFTokenCount.put(from, value.subtract(ONE));
    }

    protected void addNFToken(Contract to, BigInteger tokenId) {
        idToOwner.put(tokenId, to);

        BigInteger value = ownerToNFTokenCount.get(to);
        if(value == null)
            value = ZERO;
        ownerToNFTokenCount.remove(to);
        ownerToNFTokenCount.put(to, value.add(ONE));
    }

    protected void clearApproval(BigInteger tokenId) {
        if(idToApproval.get(tokenId) != null)
            idToApproval.remove(tokenId);
    }

    protected @FromContract void canTransfer(BigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner == caller() ||
                        idToApproval.get(tokenId) != null && idToApproval.get(tokenId) == caller() ||
                        (ownerToOperators.get(tokenOwner) != null &&
                            ownerToOperators.get(tokenOwner).getOrDefault(caller(),false)),
                "ERC721: Caller not owner or approved or operator");
    }

    protected @FromContract void canOperate(BigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner == caller() ||
                        (ownerToOperators.get(tokenOwner) != null &&
                                ownerToOperators.get(tokenOwner).getOrDefault(caller(),false)),
                "ERC721: Not owner or operator");
    }

    @Override
    public IERC721View snapshot() {
        return this;
    }

    protected void mint(Contract to, BigInteger tokenId) {
        require(to != null, "ERC721: account not valid");
        require(ownerOf(tokenId) == null, "ERC721: NFT already exists");

        addNFToken(to, tokenId);
        event(new Transfer(null, to, tokenId));
    }

    protected void burn(BigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != null, "ERC721: Not valid NFT");
        clearApproval(tokenId);

        removeNFToken(tokenOwner, tokenId);
        event(new Transfer(tokenOwner, null, tokenId));
    }

    public @View String getName(){ return this.name; }

    public @View String getSymbol(){ return this.symbol; }
}
