package com.sharelink.demo.service;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.SessionEntity;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.repository.SessionRepository;
import com.sharelink.demo.repository.ShareObjectRepository;
import com.sharelink.demo.service.tools.StringId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareObjectService {

    private static final int MAX_CODE_VAL = 9999;

    @Autowired
    ShareObjectRepository shareObjectRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Transactional
    public CreatedShareObjectDTO createNewShareObject (NewShareObjectDTO shareData, HttpSession httpSession){
        Optional<ShareObjectEntity> lastUploaded = shareObjectRepository.findTopByOrderByIdDesc();
        int newId = 0;
        if (lastUploaded.isPresent()){
            ShareObjectEntity lastObject = lastUploaded.get();
            int lastCode = Integer.parseInt(lastObject.getDisplayCode());
            newId = lastCode<=MAX_CODE_VAL ? lastCode+1 : 0;
        }

        ShareObjectEntity newObjectEntity = ShareObjectEntity.builder()
                .displayCode(StringId.parseStringId(newId))
                .shareText(shareData.getShareObject())
                .creationTime(LocalDateTime.now())
                .build();

        shareObjectRepository.save(newObjectEntity);
        sessionRepository.save(SessionEntity
                .builder()
                .objectId(newObjectEntity.getId())
                .sessionId(httpSession.getId())
                .build()
        );

        return CreatedShareObjectDTO.builder()
                .displayCode(StringId.parseStringId(newId))
                .shareObject(newObjectEntity.getShareText())
                .createdTime(newObjectEntity.getCreationTime())
                .id(newObjectEntity.getId())
                .build();
    }

    @Transactional
    public List<CreatedShareObjectDTO> getShareObjects (Pageable pageable){
        return shareObjectRepository.findAll(pageable).get()
            .map(p -> new CreatedShareObjectDTO(p.getDisplayCode(), p.getShareText(), p.getCreationTime(), p.getId()))
            .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ShareObjectEntity> getShareObject (String displayCode){
        return shareObjectRepository.findOneByDisplayCode(displayCode);
    }

    @Transactional
    public List<CreatedShareObjectDTO> searchLike(String displayCode){
        List<ShareObjectEntity> entities = shareObjectRepository.findByDisplayCodeStartsWith(displayCode);
        return entities.stream().map(p -> new CreatedShareObjectDTO(p.getDisplayCode(), p.getShareText(), p.getCreationTime(), p.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CreatedShareObjectDTO modifyShareObject (NewShareObjectDTO newShareObjectDTO, String sessionId, long id) {
        if (sessionRepository.existsBySessionIdAndObjectId(sessionId, id)) {
            ShareObjectEntity entity = shareObjectRepository.getOne(id);
            entity.setShareText(newShareObjectDTO.getShareObject());
            shareObjectRepository.save(entity);

            return CreatedShareObjectDTO.builder()
                    .id(entity.getId())
                    .shareObject(entity.getShareText())
                    .createdTime(entity.getCreationTime())
                    .displayCode(entity.getDisplayCode())
                    .build();
        } else
            return null;
    }

    @Transactional
    public ResponseEntity<String> deleteShareObject(long id, HttpSession session){
        if (sessionRepository.existsBySessionIdAndObjectId(session.getId(), id)){
            shareObjectRepository.deleteById(id);
            sessionRepository.deleteByObjectId(id);
            if (sessionRepository.countBySessionId(session.getId())==0) {
                session.invalidate();
                sessionRepository.deleteAllBySessionId(session.getId());
            }
        } else
            return new ResponseEntity<>("Error: Record not found", HttpStatus.FORBIDDEN);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }
}
