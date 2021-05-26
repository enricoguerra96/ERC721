package io.takamaka.ERC721;

import io.takamaka.code.lang.FromContract;

import java.math.BigInteger;


public class NFTMarket extends ERC721{
    public NFTMarket(String name, String symbol){
        super(name, symbol);
    }

    public @FromContract void mint(BigInteger tokenId){
        super.mint(caller(), tokenId);
    }

    public void burn(BigInteger tokenId){
        super.burn(tokenId);
    }
}