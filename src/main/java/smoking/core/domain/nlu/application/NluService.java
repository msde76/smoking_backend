package smoking.core.domain.nlu.application;

import smoking.core.domain.nlu.dto.NluResponseDTO;

public interface NluService {

    NluResponseDTO.ParseDto parseCommand(String commandText);
}