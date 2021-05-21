package io.takamaka.ERC721;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;
import io.takamaka.code.util.StorageMap;
import io.takamaka.code.util.StorageTreeMap;

import static io.takamaka.code.lang.Takamaka.event;
import static io.takamaka.code.lang.Takamaka.require;

public class ERC721 extends Contract implements IERC721{
    private final StorageMap<UnsignedBigInteger, Contract> idToOwner = new StorageTreeMap<>();
    private final StorageMap<UnsignedBigInteger, Contract> idToApproval = new StorageTreeMap<>();
    private final StorageMap<Contract, UnsignedBigInteger> ownerToNFTokenCount = new StorageTreeMap<>();
    private final StorageMap<Contract, StorageMap<Contract, Boolean>> ownerToOperators = new StorageTreeMap<>();

    public final UnsignedBigInteger ZERO = new UnsignedBigInteger("0");
    private final String name;
    private final String symbol;

    public ERC721(String name, String symbol){
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public @View UnsignedBigInteger balanceOf(Contract owner) {
        return ownerToNFTokenCount.getOrDefault(owner, ZERO);
    }

    @Override
    public @View Contract ownerOf(UnsignedBigInteger tokenId){ return idToOwner.get(tokenId); }

    @Override
    public @FromContract void approve(Contract approved, UnsignedBigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != approved, "ERC721: Approved is owner");
        require(tokenOwner != null, "ERC721: Not valid NFT");
        canOperate(tokenId);

        idToApproval.putIfAbsent(tokenId, approved);
        event(new Approval(tokenOwner, approved, tokenId));
    }

    @Override
    public @View Contract getApproved(UnsignedBigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != null, "ERC721: Not valid NFT");
        return idToApproval.get(tokenId);
    }

    @Override
    public void setApprovalForAll(Contract operator, boolean approved) {
        StorageMap<Contract, Boolean> innerMap = ownerToOperators.get(caller());
        if(innerMap == null) {
            ownerToOperators.put(caller(), innerMap = new StorageTreeMap<>());
        }
        innerMap.put(operator, approved);

        event(new ApprovalForAll(caller(), operator, approved));
    }

    @Override
    public @View boolean isApprovedForAll(Contract owner, Contract operator) {
        return ownerToOperators.get(owner).get(operator);
    }

    @Override
    public @FromContract void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {
        _safeTransferFrom(from, to, tokenId, (byte) 0);
    }

    @Override
    public @FromContract void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte data) {
        _safeTransferFrom(from, to, tokenId, data);
    }

    @Override
    public @FromContract void TransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {
        transfer(from, to, tokenId);
    }

    protected void _safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte data){
        transfer(from, to, tokenId);
        //TODO: ERC721 receiver
    }

    protected void transfer(Contract from, Contract to, UnsignedBigInteger tokenId){
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

    protected void removeNFToken(Contract from, UnsignedBigInteger tokenId) {
        idToOwner.remove(tokenId);

        UnsignedBigInteger value = ownerToNFTokenCount.get(from);
        ownerToNFTokenCount.remove(from);
        ownerToNFTokenCount.put(from, value.subtract(UnsignedBigInteger.valueOf(1)));
    }

    protected void addNFToken(Contract to, UnsignedBigInteger tokenId) {
        idToOwner.put(tokenId, to);

        UnsignedBigInteger value = ownerToNFTokenCount.get(to);
        ownerToNFTokenCount.remove(to);
        ownerToNFTokenCount.put(to, value.add(UnsignedBigInteger.valueOf(1)));
    }

    protected void clearApproval(UnsignedBigInteger tokenId) {
        if(idToApproval.get(tokenId) != null)
            idToApproval.remove(tokenId);
    }

    protected void canTransfer(UnsignedBigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner == caller() ||
                        idToApproval.get(tokenId) == caller() ||
                        ownerToOperators.get(tokenOwner).get(caller()),
                "ERC721: Caller not owner or approved or operator");
    }

    protected void canOperate(UnsignedBigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner == caller() || ownerToOperators.get(tokenOwner).get(caller()),
                "ERC721: Not owner or operator");
    }

    @Override
    public IERC721View snapshot() {
        return this;
    }

    protected void mint(Contract to, UnsignedBigInteger tokenId) {
        require(to != null, "ERC721: account not valid");
        require(ownerOf(tokenId) == null, "ERC721: NFT already exists");

        addNFToken(to, tokenId);
        event(new Transfer(null, to, tokenId));
    }

    protected void burn(UnsignedBigInteger tokenId) {
        Contract tokenOwner = ownerOf(tokenId);
        require(tokenOwner != null, "ERC721: Not valid NFT");
        clearApproval(tokenId);

        removeNFToken(tokenOwner, tokenId);
        event(new Transfer(tokenOwner, null, tokenId));
    }

    public @View String getName(){
        return this.name;
    }

    public @View String getSymbol(){
        return this.symbol;
    }
}
