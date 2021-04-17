package com.sharelink.demo.service.tools.mapper;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShareObjectDTOMapper {

    @Autowired
    ModelMapper modelMapper;

    public CreatedShareObjectDTO mapShareEntityToDTO (ShareObjectEntity entity){
        CreatedShareObjectDTO createdShareObjectDTO = modelMapper.map(entity, CreatedShareObjectDTO.class);
        createdShareObjectDTO.setDisplayCode(StringId.parseStringId(entity.getDisplayCode()));
        return createdShareObjectDTO;
    }

}
