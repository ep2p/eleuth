package com.github.ep2p.eleuth.service.row;

import com.github.ep2p.eleuth.model.entity.memory.NodeSessionEntity;
import com.github.ep2p.eleuth.repository.memory.NodeSessionRepository;
import lab.idioglossia.row.server.context.RowContext;
import lab.idioglossia.row.server.context.RowContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RowConnectionMapper {
    private final NodeSessionRepository nodeSessionRepository;

    public RowConnectionMapper(NodeSessionRepository nodeSessionRepository) {
        this.nodeSessionRepository = nodeSessionRepository;
    }

    public void addMapping(String nodeId){
        try {
            RowContext context = RowContextHolder.getContext();
            if(context.isRowRequest()){
                nodeSessionRepository.save(NodeSessionEntity.builder()
                        .nodeId(nodeId)
                        .sessionId(context.getRowUser().getSessionId())
                        .userId(context.getRowUser().getUserId())
                        .build());
            }
        }catch (Exception e){
            log.error("Could not map node to session", e);
        }
    }
}
