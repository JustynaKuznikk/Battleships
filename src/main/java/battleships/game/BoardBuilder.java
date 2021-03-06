package battleships.game;

import battleships.ships.Ship;
import battleships.ships.ShipsFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class BoardBuilder {

    private Board board;
    private int[] availableShips = {2, 2, 1, 1, 1};

    private Set<Field> availableFields = new HashSet<>();

    public BoardBuilder() {
        board = new Board();
        for (Field[] row : board.getFields()) {
            for (Field field : row) {
                availableFields.add(field);
            }
        }
    }

    public Board build() {
        int sum = IntStream.of(availableShips).sum();
        if (sum > 0) throw new IllegalStateException("Nie ustawiono wszystkich statków");
        return board;
    }

    public BoardBuilder submarine(char row, int col, boolean vertical) {
        placeShip(ShipsFactory.submarine(), row, col, vertical);
        return this;
    }

    public BoardBuilder destroyer(char row, int col, boolean vertical) {
        placeShip(ShipsFactory.destroyer(), row, col, vertical);
        return this;
    }

    public BoardBuilder cruiser(char row, int col, boolean vertical) {
        placeShip(ShipsFactory.cruiser(), row, col, vertical);
        return this;
    }

    public BoardBuilder warship(char row, int col, boolean vertical) {
        placeShip(ShipsFactory.warship(), row, col, vertical);
        return this;
    }

    public BoardBuilder carrier(char row, int col, boolean vertical) {
        placeShip(ShipsFactory.carrier(), row, col, vertical);
        return this;
    }

    private void placeShip(Ship ship, char row, int col, boolean vertical) {
        requireShipIsAvailable(ship);
        Set<Field> fieldsToUse = computeFields(ship, row, col, vertical);
        requireFieldsAreAvailable(fieldsToUse);
        takeFields(ship, fieldsToUse);
        removeUnavailableFields(fieldsToUse);
        markShipAsUsed(ship);
    }

    private void markShipAsUsed(Ship ship) {
        availableShips[ship.getSize() - 1]--;
    }

    private void removeUnavailableFields(Set<Field> usedFields) {
        Set<Field> unavailableFields = new HashSet<>();
        for (Field field : usedFields) {
            unavailableFields.add(field);
            unavailableFields.add(new Field((char) (field.getRow() - 1), field.getCol()));
            unavailableFields.add(new Field((char) (field.getRow() + 1), field.getCol()));
            unavailableFields.add(new Field(field.getRow(), field.getCol() - 1));
            unavailableFields.add(new Field(field.getRow(), field.getCol() + 1));
        }
        availableFields.removeAll(unavailableFields);
    }

    private void takeFields(Ship ship, Set<Field> fieldsToUse) {
        for (Field field : fieldsToUse) {
            board.setShip(ship, field.getRow(), field.getCol());
        }
    }

    private void requireFieldsAreAvailable(Set<Field> fieldsToUse) {
        if (!availableFields.containsAll(fieldsToUse)) {
            throw new IllegalArgumentException("Nie wszystkie potrzebne pola są dostępne");
        }
    }

    private Set<Field> computeFields(Ship ship, char row, int col, boolean vertical) {
        Set<Field> fields = new HashSet<>();
        if (vertical) {
            for (int i = col; i < col + ship.getSize(); i++) {
                fields.add(new Field(row, i));
            }
        }
        else {
            for (int i = row; i < row + ship.getSize(); i++) {
                fields.add(new Field((char) i, col));
            }
        }
        return fields;
    }

    private void requireShipIsAvailable(Ship ship) {
        if (availableShips[ship.getSize() - 1] <= 0) throw new IllegalArgumentException("Wszystkie statki typu " + ship.getName() + "zostały już wykorzystane");
    }
}
