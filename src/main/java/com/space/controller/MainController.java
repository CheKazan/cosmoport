package com.space.controller;

import com.space.model.Ship;
import com.space.service.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private Services services;

    @GetMapping("rest/ships")
    public List<Ship> getShips(String name, String planet,
                               String shipType, Long after,
                               Long before, Boolean isUsed,
                               Double minSpeed, Double maxSpeed,
                               Integer minCrewSize, Integer maxCrewSize,
                               Double minRating, Double maxRating,
                               Integer pageNumber, Integer pageSize,
                               String order){
        if(pageNumber==null) pageNumber=0;
        if(pageSize==null) pageSize=3;
        Comparator<Ship> comparator = Comparator.comparing(Ship::getId);
        if(order!= null) {
            if (ShipOrder.valueOf(order) == ShipOrder.DATE) comparator = Comparator.comparing(Ship::getProdDate);
            else if (ShipOrder.valueOf(order) == ShipOrder.RATING) comparator = Comparator.comparing(Ship::getRating);
            else if (ShipOrder.valueOf(order) == ShipOrder.SPEED) comparator = Comparator.comparing(Ship::getSpeed);
        }
        return services.getShips(name,planet,shipType,after,before,
                isUsed,minSpeed,maxSpeed,minCrewSize,maxCrewSize,
                minRating,maxRating,pageNumber,pageSize,comparator);
    }

    @GetMapping("rest/ships/count")
    public Integer getShipsCount(String name, String planet,
                                 String shipType, Long after,
                                 Long before, Boolean isUsed,
                                 Double minSpeed, Double maxSpeed,
                                 Integer minCrewSize, Integer maxCrewSize,
                                 Double minRating, Double maxRating,
                                 Integer pageNumber, Integer pageSize,
                                 String order){
        return services.getShips(name,planet,shipType,after,before,
                isUsed,minSpeed,maxSpeed,minCrewSize,maxCrewSize,
                minRating,maxRating,0,Integer.MAX_VALUE,Comparator.comparing(Ship::getId)).size();
    }

    @PostMapping("rest/ships")
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship){
        Ship newShip = services.addShip(ship);
        if(newShip== null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(newShip);
    }

    @GetMapping("rest/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable Long id){
        if(!(id instanceof Number && id>0 && id==Math.round(id))) return ResponseEntity.badRequest().build();
        Ship ship = services.getShipById(id);
        if(ship== null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ship);
    }

    @PostMapping("rest/ships/{id}")
    public ResponseEntity<Ship> updateShipById(@PathVariable Long id, @RequestBody Ship ship){
        if(!(id instanceof Number && id>0 && id==Math.round(id))) return ResponseEntity.badRequest().build();
        Ship oldShip = services.getShipById(id);
        if(oldShip == null) return ResponseEntity.notFound().build();
        HttpStatus httpStatus = services.updateShipById(oldShip,ship);
        return new ResponseEntity<Ship>(oldShip,httpStatus);
    }

    @DeleteMapping("rest/ships/{id}")
    public ResponseEntity deleteShip(@PathVariable Long id){
        if(!(id instanceof Number && id>0 && id==Math.round(id))) return ResponseEntity.badRequest().build();
        Ship ship = services.getShipById(id);
        if(ship == null) return ResponseEntity.notFound().build();
        services.deleteShip(id);
        return ResponseEntity.ok().build();
    }


}


