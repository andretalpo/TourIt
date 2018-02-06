package br.com.marcus.fernanda.andre.tourit.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by André on 30/10/2017.
 */

class ConstantesTipoLocal {

    private Map<String, String> tipos = new HashMap<>();

    ConstantesTipoLocal(){
        tipos.put("establishment", "Estabelecimento");
        tipos.put("grocery_or_supermarket", "Mercado");
        tipos.put("health", "Saúde");
        tipos.put("food", "Comida");
        tipos.put("place_of_worship", "Adoração");
        tipos.put("airport", "Aeroporto");
        tipos.put("amusement_park", "Parque de Diversões");
        tipos.put("aquarium", "Aquário");
        tipos.put("art_gallery", "Galeria de Arte");
        tipos.put("bakery", "Padaria");
        tipos.put("bank", "Banco");
        tipos.put("bar", "Bar");
        tipos.put("beauty_salon", "Salão de Beleza");
        tipos.put("book_store", "Livraria");
        tipos.put("bus_station", "Estação de Ônibus");
        tipos.put("cafe", "Café");
        tipos.put("campground", "Acampamento");
        tipos.put("casino", "Cassino");
        tipos.put("cemetery", "Cemitério");
        tipos.put("church", "Igreja");
        tipos.put("city_hall", "Prefeitura");
        tipos.put("clothing_store", "Loja de Roupas");
        tipos.put("convenience_store", "Conveniência");
        tipos.put("courthouse", "Tribunal");
        tipos.put("department_store", "Loja de Departamentos");
        tipos.put("electronics_store", "Loja de Eletrônicos");
        tipos.put("embassy", "Embaixada");
        tipos.put("fire_station", "Bombeiro");
        tipos.put("furniture_store", "Loja de Móveis");
        tipos.put("gym", "Academia");
        tipos.put("hair_care", "Cuidado com Cabelos");
        tipos.put("hardware_store", "Loja de Hardware");
        tipos.put("hindu_temple", "Templo Hindu");
        tipos.put("home_goods_store", "Loja");
        tipos.put("hospital", "Hospital");
        tipos.put("jewelry_store", "Loja de Jóias");
        tipos.put("library", "Biblioteca");
        tipos.put("liquor_store", "Loja de Bebidas");
        tipos.put("local_government_office", "Governo");
        tipos.put("lodging", "Alojamento");
        tipos.put("meal_delivery", "Delivery de Comida");
        tipos.put("zoo", "Zoológico");
        tipos.put("university", "Universidade");
        tipos.put("travel_agency", "Agência de Viagens");
        tipos.put("train_station", "Estação de Trem");
        tipos.put("synagogue", "Sinagoga");
        tipos.put("subway_station", "Estação de Metrô");
        tipos.put("store", "Loja");
        tipos.put("storage", "Armazém");
        tipos.put("stadium", "Estádio");
        tipos.put("spa", "Spa");
        tipos.put("shopping_mall", "Shopping");
        tipos.put("shoe_store", "Loja de Calçados");
        tipos.put("school", "Escola");
        tipos.put("restaurant", "Restaurante");
        tipos.put("post_office", "Correio");
        tipos.put("police", "Polícia");
        tipos.put("pharmacy", "Farmácia");
        tipos.put("parking", "Estacionamento");
        tipos.put("park", "Parque");
        tipos.put("night_club", "Balada");
        tipos.put("museum", "Museu");
        tipos.put("movie_theater", "Cinema");
        tipos.put("mosque", "Mesquita");
        tipos.put("natural_feature", "Natureza");
    }

    public Map<String, String> getTipos() {
        return tipos;
    }

    public void setTipos(Map<String, String> tipos) {
        this.tipos = tipos;
    }
}
