package be.esi.prj;

import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.repository.ItemRepository;
import be.esi.prj.repository.ItemRepositoryImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL resource = Main.class.getResource("/view/UsersView.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("Scanner Borrow Manager");
        stage.show();


        ItemRepository repo = new ItemRepositoryImpl();

        // 1. Ajouter un objet test
        Item testItem = new Item("QR123",  ItemCategory.MESURE, ItemStatus.AVAILABLE, "c:/qr/123.png");
        repo.insert(testItem);

        Item test2Item = new Item("Imprimmante", ItemCategory.MESURE, ItemStatus.AVAILABLE, "c:/qr/123.png");

        repo.insert(test2Item);
        // 2. Vérifier si on le récupère
        System.out.println("Objets en base : " + repo.findAll().size());
        for(Item i : repo.findAll()) {
            System.out.println("Trouvé : " + i.getName());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
