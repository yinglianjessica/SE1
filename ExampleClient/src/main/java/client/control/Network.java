package client.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import client.exceptions.ResponseEnvelopeException;

import org.springframework.http.HttpMethod;

import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import reactor.core.publisher.Mono;

public class Network {
	Logger logger = LoggerFactory.getLogger(Control.class);

	private String serverBaseUrl;
	private String gameId;
	private WebClient baseWebClient;
	private String playerId;
    	

	public Network(String serverBaseUrl, String gameId) {
		this.serverBaseUrl = serverBaseUrl;
		this.gameId = gameId;
		this.baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) // the network protocol uses
																							// XML
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}
	
	public GameState requestGameState() throws ResponseEnvelopeException {
		logger.info("Info: Requesting game state from Server");
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			logger.error("Thread sleep interrupted" + e.getMessage());
		}
		GameState serverGameState = null;
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + this.gameId + "/states/" + this.playerId).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<GameState> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			logger.error("Error in requestGameState(): " + resultReg.getExceptionMessage());
			throw new ResponseEnvelopeException("Error in requestGameState(): " + resultReg.getExceptionMessage());
		}
		if (!resultReg.getData().isPresent()) {
			throw new ResponseEnvelopeException("game state in requestGameState() is empty");
		} 
		serverGameState = resultReg.getData().get();
		return serverGameState;
	}
	
	public String playerRegistration() throws ResponseEnvelopeException {
		logger.info("Info: Regististrating player to Server");
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			logger.error("Thread sleep interrupted" + e.getMessage());
		}
		PlayerRegistration playerReg = new PlayerRegistration("Yinglian-Jessica", "Huynh",
				"yinglianjh02");
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/players")
				.body(BodyInserters.fromValue(playerReg)) 
				.retrieve().bodyToMono(ResponseEnvelope.class); 
		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();
		
		if (resultReg.getState() == ERequestState.Error) {
			throw new ResponseEnvelopeException("playerRegistration() failed. " + resultReg.getExceptionMessage());
		} 
		if (!resultReg.getData().isPresent()) {
			throw new ResponseEnvelopeException("playerId in playerRegistration() is empty");
		} 
		UniquePlayerIdentifier uniqueID = resultReg.getData().get();
		this.playerId = uniqueID.getUniquePlayerID();			
		logger.info("Info: Player registrated successfully with player ID: " + uniqueID.getUniquePlayerID());
		return playerId;
	}
	
	public void sendMap(PlayerHalfMap playerHalfMap) throws ResponseEnvelopeException {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			logger.error("Thread sleep interrupted" + e.getMessage());
		}
		logger.info("Info: Sending playerHalfMap to Server");
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + this.gameId + "/halfmaps")
				.body(BodyInserters.fromValue(playerHalfMap)).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<GameState> resultReg = webAccess.block();
		if (resultReg.getState() == ERequestState.Error) {
			throw new ResponseEnvelopeException("sendMap() failed. " + resultReg.getExceptionMessage());
		}
		else {
			logger.info("Sending map successful");
		}
	}

	public void sendPlayerMove(PlayerMove playerMove) throws ResponseEnvelopeException {
		logger.info("Info: Sending Move to Server");
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			logger.error("Thread sleep interrupted" + e.getMessage());
		}
		Mono<ResponseEnvelope> webAccess = (baseWebClient.method(HttpMethod.POST))
				.uri("/" + gameId + "/moves").header("accept", "application/xml")
				.body(BodyInserters.fromValue(playerMove)).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<GameState> resultReg = webAccess.block();
		if (resultReg.getState() == ERequestState.Error) {
			throw new ResponseEnvelopeException("sendPlayerMove() failed. " + resultReg.getExceptionMessage());
		}
		else {
			logger.info("Sending Move to server successful");
		}
	}
}
