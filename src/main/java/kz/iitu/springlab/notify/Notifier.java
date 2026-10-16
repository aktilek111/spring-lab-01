package kz.iitu.springlab.notify;

public interface Notifier {

    String send(String message); // возвращает то, что было "отправлено"

    String channel(); // имя канала для отчёта
}
 