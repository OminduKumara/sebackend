package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.CreateRoomDto;
import com.backend.mybungalow.dto.RoomDto;
import com.backend.mybungalow.dto.UpdateRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomDto createRoom(CreateRoomDto dto);
    RoomDto updateRoom(Long id, UpdateRoomDto dto);
    void deleteRoom(Long id);
    RoomDto getRoom(Long id);
    Page<RoomDto> listRooms(Pageable pageable);
}
