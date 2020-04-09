package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Services {
    @Autowired
    private ShipRepository shipRepository;


    public List<Ship> getShips(String name, String planet, String shipType, Long after, Long before,
                               Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                               Double minRating, Double maxRating, Integer pageNumber, Integer pageSize, Comparator<Ship> comparator){
        return  shipRepository.findAll().stream()
                .filter(name!=null ? s-> s.getName().contains(name): s -> true )
                .filter(planet!=null ? s-> s.getPlanet().contains(planet):s -> true )
                .filter(shipType!=null ? s-> s.getShipType().equals(ShipType.valueOf(shipType)):s->true)
                .filter(after!=null ? s-> s.getProdDate().after(new Date(after)) : s->true)
                .filter(before!=null ? s-> s.getProdDate().before(new Date(before)): s->true)
                .filter(isUsed!=null ? s-> s.isUsed().equals(isUsed): s->true)
                .filter(minSpeed!=null ? s-> s.getSpeed()>=minSpeed: s->true)
                .filter(maxSpeed!=null ? s-> s.getSpeed()<=maxSpeed: s->true)
                .filter(minCrewSize!=null ? s-> s.getCrewSize()>=minCrewSize: s->true)
                .filter(maxCrewSize!=null ? s-> s.getCrewSize()<=maxCrewSize: s->true)
                .filter(minRating!=null ? s-> s.getRating()>=minRating: s->true)
                .filter(maxRating!=null ? s-> s.getRating()<=maxRating: s->true)
                .skip(pageSize*pageNumber)
                .limit(pageSize)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    public Ship addShip(Ship ship){
        Ship newShip = null;
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null
                || ship.getName().length()>50
                || ship.getPlanet().length()>50
                || ship.getName().equals("")
                || ship.getPlanet().equals("")
                || ship.getSpeed()<0.01
                || ship.getSpeed()>0.99
                || ship.getCrewSize()<1
                || ship.getCrewSize()>9999
                || ship.getProdDate().getTime()<0
                || ship.getProdDate().getTime()<new GregorianCalendar(2800, 0,1).getTimeInMillis()
                || ship.getProdDate().getTime()>new GregorianCalendar(3019, 12,31).getTimeInMillis())
            return newShip;
        newShip = new Ship();
        newShip.setName(ship.getName());
        newShip.setPlanet(ship.getPlanet());
        newShip.setShipType(ship.getShipType());
        newShip.setProdDate(ship.getProdDate());
        newShip.setUsed(ship.isUsed()!=null ? ship.isUsed() : false);
        newShip.setSpeed(ship.getSpeed());
        newShip.setCrewSize(ship.getCrewSize());
        newShip.setRating(new BigDecimal(Double.toString((80d * newShip.getSpeed()*(newShip.isUsed()? 0.5 : 1 ))/(3019d-(newShip.getProdDate().getYear()+1900)+1d))).setScale(2, RoundingMode.HALF_UP).doubleValue());
        shipRepository.save(newShip);
        return newShip;
    }
    public void deleteShip(Long id){
        shipRepository.deleteById(id);
    }

    public Ship getShipById(Long id){
        Optional<Ship> ship = shipRepository.findById(id);
        if (ship.isPresent()) return ship.get();
        else return null;
    }

    public HttpStatus updateShipById(Ship oldShip, Ship newShip){
        if (newShip.getName() == null
                && newShip.getPlanet() == null && newShip.getShipType() == null
                && newShip.getCrewSize() == null && newShip.getSpeed() == null && newShip.getProdDate() == null)
            return HttpStatus.OK;

       if (newShip.getName()!=null){
               if( newShip.getName().length()>50 || newShip.getName().equals("")) return HttpStatus.BAD_REQUEST;
               oldShip.setName(newShip.getName());
       }

       if (newShip.getPlanet()!=null){
           if(newShip.getPlanet().length()>50 || newShip.getPlanet().equals("")) return HttpStatus.BAD_REQUEST;
           oldShip.setPlanet(newShip.getPlanet());
       }

       if(newShip.getShipType()!=null) oldShip.setShipType(newShip.getShipType());

       if (newShip.getProdDate() != null){
              if(newShip.getProdDate().getTime()<new GregorianCalendar(2800, 0,1).getTimeInMillis()
                    || newShip.getProdDate().getTime()>new GregorianCalendar(3019, 12,31).getTimeInMillis())
                  return HttpStatus.BAD_REQUEST;
           oldShip.setProdDate(newShip.getProdDate());
       }
       if(newShip.isUsed()!=null) oldShip.setUsed(newShip.isUsed());
       if(newShip.getSpeed() != null){
            if(newShip.getSpeed()<0.01
                || newShip.getSpeed()>0.99) return HttpStatus.BAD_REQUEST;
            oldShip.setSpeed(newShip.getSpeed());
       }
        if(newShip.getCrewSize() != null){
               if( newShip.getCrewSize()<1
                || newShip.getCrewSize()>9999) return HttpStatus.BAD_REQUEST;
            oldShip.setCrewSize(newShip.getCrewSize());
        }
        oldShip.setRating(new BigDecimal(Double.toString((80d * oldShip.getSpeed()*(oldShip.isUsed()? 0.5 : 1 ))/(3019d-(oldShip.getProdDate().getYear()+1900)+1d))).setScale(2, RoundingMode.HALF_UP).doubleValue());
        shipRepository.save(oldShip);
        return HttpStatus.OK;
    }
}
