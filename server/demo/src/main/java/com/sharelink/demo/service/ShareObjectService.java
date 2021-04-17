package com.sharelink.demo.service;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.repository.ShareObjectRepository;
import com.sharelink.demo.service.tools.mapper.StringId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareObjectService {

    private static final int MAX_CODE_VAL = 9999;

    @Autowired
    ShareObjectRepository shareObjectRepository;

    public CreatedShareObjectDTO createNewShareObject (NewShareObjectDTO shareData){
        Optional<ShareObjectEntity> lastUploaded = shareObjectRepository.findTopByOrderByIdDesc();
        int newId = 0;
        if (lastUploaded.isPresent()){
            ShareObjectEntity lastObject = lastUploaded.get();
            newId = lastObject.getDisplayCode()<=MAX_CODE_VAL ? lastObject.getDisplayCode()+1 : 0;
        }

        ShareObjectEntity newObjectEntity = ShareObjectEntity.builder()
                .displayCode(newId)
                .shareText(shareData.getShareObject())
                .creationTime(LocalDateTime.now())
                .build();

        shareObjectRepository.save(newObjectEntity);

        return CreatedShareObjectDTO.builder()
                .displayCode(StringId.parseStringId(newId))
                .shareObject(newObjectEntity.getShareText())
                .createdTime(newObjectEntity.getCreationTime()).build();
    }

    public List<CreatedShareObjectDTO> getShareObjects (Pageable pageable){
        return shareObjectRepository.findAll(pageable).get()
            .map(p -> new CreatedShareObjectDTO(StringId.parseStringId(p.getDisplayCode()), p.getShareText(), p.getCreationTime(), p.getId()))
            .collect(Collectors.toList());
    }

    public Optional<ShareObjectEntity> getShareObject (int displayCode){
        return shareObjectRepository.findAllByDisplayCode(displayCode);
    }
    public ResponseEntity<String> deleteShareObject(long id){
        if (shareObjectRepository.existsById(id)){
            shareObjectRepository.deleteById(id);
        } else
            return new ResponseEntity<>("Error: Record not found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }
}
