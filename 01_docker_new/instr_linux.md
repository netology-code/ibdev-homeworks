:house: [На главную](https://github.com/netology-code/ibdev-homeworks/tree/master/01_docker_new)

# Инструкция по настройке компонентов Docker в ВМ

## Вводная информация

В этом задании вам предстоит работать с контейнерами на основе ОС Linux. Рекомендуем выполнять задания в этом ДЗ на виртуальных машинах Kali Linux или Ubuntu, которые установленны на платформе виртуализации VirtualBox. Вы будете работать в терминалах Linux и с командной оболочкой bash.

-----

## Подготовка к работе

**1. Технические ресурсы**

Проверьте свои технические ресурсы. Они общие для курса, по которому вы проходите обучение, и в целом достаточны для последующей профессиональной деятельности ИБ:
- процессор - Intel/AMD не менее 4 ядер,
- ОЗУ - не менее 8 Гб,
- рекомендованная хостовая ОС - MS Windows 7/10 (допускается ОС Linux, MacOS),
- рекомендованное свободное место на диске HDD/SSD - около 100 Гб.

**2. Виртуализация**

Если на текущий момент вы до сих пор не развернули на хостовой ОС платформу виртуализации VirtualBox, самое время сделать это. Вы можете загрузить бинарный установщик VirtualBox под вашу хостовую ОС по [ссылке 1](https://www.virtualbox.org/wiki/Downloads) или [ссылке 2](https://download.virtualbox.org/virtualbox/).

_Вариант 1. Kali Linux_

Процедура установки VirtualBox на хостовую ОС Windows простая и выполняется в несколько кликов. Для загрузки актуального образа виртуальной машины Kali Linux перейдите и скачайте его [отсюда](https://www.kali.org/get-kali/#kali-virtual-machines).

Вот ещё несколько полезных ссылок для Kali Linux:
- [процедура](https://www.kali.org/docs/virtualization/install-virtualbox-guest-vm/) установки виртуальной машины Kali Linux на VirtualBox с хостовой ОС Windows,
- другие варианты Kali Linux и установок на разные ОС и платформы виртуализации: [ссылка 1](https://www.kali.org/get-kali/#kali-platforms), [ссылка 2](https://www.kali.org/docs/virtualization/).

_Вариант 2. Ubuntu_

Для загрузки актуального образа виртуальной машины Ubuntu перейдите и скачайте его по [ссылке](https://ubuntu.com/download/desktop).

С процедурой установки виртуальной машины Ubuntu на VirtualBox с хостовой ОС Windows можно ознакомиться [здесь](https://ubuntu.com/tutorials/how-to-run-ubuntu-desktop-on-a-virtual-machine-using-virtualbox#1-overview).

**3. Проверка настроек сетевых интерфейсов**

После установки виртуальной машины проверьте наличие и правильность настройки сетевых интерфейсов выбранной виртуальной машины Linux. Для выполнения ДЗ необходимо, чтобы были настроены два сетевых интерфейса: NAT и внутренняя сеть. 

Что нужно для проверки настроек?

1. Откройте первый терминал виртуальной машины Linux и выполните команду проверки текущего состояния сетевых интерфейсов:

- `ifconfig` (на Kali Linux):

<img src="https://github.com/netology-code/ibdev-homeworks/blob/master/01_docker_new/pic/ifconfig.png" width="700">

- `ip a` (на Ubuntu):

![](https://github.com/netology-code/ibdev-homeworks/blob/master/01_docker_new/pic/ip%20a.png)

2. Проверьте доступность интернета пингами одного или нескольких общедоступных внешних сайтов, например,

`ping -с 5 ya.ru`:

<img src="https://github.com/netology-code/ibdev-homeworks/blob/master/01_docker_new/pic/ping%20-%D1%81%205%20ya.png" width="700">

3. Если на ваших виртуальных машинах необходимые сетевые интерфейсы отсутствуют, либо настройки не соответствуют требуемым, выполните их настройку и повторите проверку текущего состояния сетевых интерфейсов и доступность интернета представленными выше командами. 

*Примечание. На скриншотах, демонстрируемых в инструкциях, показаны примеры выполнения практических упражнений на виртуальной машине Kali Linux. Если вы выполняете задания на виртуальной машине Ubuntu, все команды и выводимые результаты будут идентичны.*

-----

:house: [На главную](https://github.com/netology-code/ibdev-homeworks/tree/master/01_docker_new)
