package server.rules;

import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;

public interface IRule {
	public void validateRegistration(String gameId, PlayerRegistration playerRegistration);
	
	public void validateMap(String gameId, PlayerHalfMap playerHalfMap);

	public void validateState(String gameId, UniquePlayerIdentifier playerId);

}
