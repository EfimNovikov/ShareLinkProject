package com.sharelink.demo.service;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.repository.ShareObjectRepository;
import com.sharelink.demo.service.tools.mapper.StringId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
            newId = lastObject.getDisplay_code()<=MAX_CODE_VAL ? lastObject.getDisplay_code()+1 : 0;
        }

        ShareObjectEntity newObjectEntity = ShareObjectEntity.builder()
                .display_code(newId)
                .shareText(shareData.getShareObject())
                .creationTime(LocalDateTime.now())
                .build();

        shareObjectRepository.save(newObjectEntity);

        return CreatedShareObjectDTO.builder()
                .id(StringId.parseStringId(newId))
                .shareObject(newObjectEntity.getShareText())
                .createdTime(newObjectEntity.getCreationTime()).build();
    }

    public List<CreatedShareObjectDTO> getShareObjects (Pageable pageable){
        return shareObjectRepository.findAll(pageable).get()
            .map(p -> new CreatedShareObjectDTO(StringId.parseStringId(p.getDisplay_code()), p.getShareText(), p.getCreationTime()))
            .collect(Collectors.toList());
    }
}
