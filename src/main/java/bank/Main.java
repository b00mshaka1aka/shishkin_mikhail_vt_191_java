package bank;

import bank.exceptions.*;

import java.io.IOException;
import java.util.Scanner;

import bank.service.impl.*;

import java.time.LocalDate;
import java.util.ArrayList;

import bank.entity.Employee;
import bank.entity.BankOffice;
import bank.service.BankService;
import bank.service.UserService;
import bank.entity.enums.StatusATM;
import bank.entity.enums.StatusOffice;

public class Main {
    static void lab1() {
        //Bank
        System.out.println("Bank:");
        BankServiceImpl bankService = new BankServiceImpl();
        bankService.create(1, "VTB");
        System.out.println(bankService.getBank());

        //Bank Office
        System.out.println("\n\nOffice:");
        BankOfficeServiceImpl bankOfficeService = new BankOfficeServiceImpl();
        bankOfficeService.create(1, "VTB_office", bankService.getBank(), "Moscow",
                StatusOffice.Work, 15000.0);
        System.out.println(bankOfficeService.getBankOffice());

        //Employee
        System.out.println("\n\nEmployee:");
        EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
        employeeService.create(1, "Mikhail", "Shishkin", LocalDate.of(2000, 10,
                        11),
                bankService.getBank(), bankOfficeService.getBankOffice(), "SEO", 100.0);
        System.out.println(employeeService.getEmployee());

        //Bank ATM
        System.out.println("\n\nATM:");
        AtmServiceImpl atmService = new AtmServiceImpl();
        atmService.create(1, "ATM_1", StatusATM.Work, Boolean.TRUE, Boolean.TRUE,
                100.0, bankService.getBank(), bankOfficeService.getBankOffice(),
                employeeService.getEmployee());
        System.out.println(atmService.getBankATM());

        //User
        System.out.println("\n\nUser:");
        UserServiceImpl userService = new UserServiceImpl();
        userService.create(1, "Albert", "Gennadievich", LocalDate.of(2000, 10,
                        11),
                "work_1");
        System.out.println(userService.getUser());

        //Payment Account
        System.out.println("\n\nPayment Account:");
        PaymentAccountServiceImpl paymentAccountService = new PaymentAccountServiceImpl();
        paymentAccountService.create(1, userService.getUser(), bankService.getBank());
        System.out.println(paymentAccountService.getPayAcc());

        //Credit Account
        System.out.println("\n\nCredit Account:");
        CreditAccountServiceImpl creditAccountService = new CreditAccountServiceImpl();
        creditAccountService.create(1, userService.getUser(), bankService.getBank(), employeeService.getEmployee(),
                paymentAccountService.getPayAcc(), LocalDate.of(2022, 11, 11), 12,
                150.0);
        System.out.println(creditAccountService.getCreditAcc());
    }

    static void lab2() throws CredAccUserException, PayAccUserException, OfficeBankException, AtmBankException,
            EmployeeBankException, UserBankException, AtmOfficeException, EmployeeOfficeException {
        ArrayList<BankServiceImpl> banks = new ArrayList<>();
        ArrayList<UserServiceImpl> users = new ArrayList<>();
        for (int i_1 = 0; i_1 < 5; i_1++) {
            BankServiceImpl bankService = new BankServiceImpl();
            bankService.create(i_1, String.format("bank_???%d", i_1));
            for (int i_2 = 0; i_2 < 3; i_2++) {
                BankOfficeServiceImpl bankOfficeService = new BankOfficeServiceImpl();
                bankOfficeService.create(i_2 + i_1, String.format("office_???%d", i_2), bankService.getBank(),
                        String.format("address_%d", i_2), StatusOffice.Work, 15000.0);
                for (int i_3 = 0; i_3 < 5; i_3++) {
                    EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
                    employeeService.create(i_3 + i_2 + i_1, String.format("Oleg_%d", i_3 + i_2 + i_1),
                            "Bernulli", LocalDate.of(2000, 10, 11), bankService
                                    .getBank(),
                            bankOfficeService.getBankOffice(), String.format("job_%d", i_3), 100.0);
                    bankOfficeService.addEmployee(employeeService);
                    bankService.addEmployee(employeeService);
                }
                AtmServiceImpl atmService = new AtmServiceImpl();
                atmService.create(i_2 + i_1, String.format("ATM_%d", i_2 + i_1), StatusATM.Work, Boolean.TRUE,
                        Boolean.TRUE, 100.0, bankOfficeService.getBankOffice().getBank(),
                        bankOfficeService.getBankOffice(), bankOfficeService.getBankOffice().getEmployees().get(1));
                bankOfficeService.addBankATM(atmService);
                bankService.addBankATM(atmService);
                bankService.addBankOffice(bankOfficeService);
            }

            UserServiceImpl userService = new UserServiceImpl();
            userService.create(i_1, String.format("Elena_%d", i_1), "Eugenievna", LocalDate.of(2000,
                    10, 11), String.format("work_%d", i_1));
            for (int i_2 = 0; i_2 < 2; i_2++) {
                PaymentAccountServiceImpl paymentAccountService = new PaymentAccountServiceImpl();
                paymentAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank());

                CreditAccountServiceImpl creditAccountService = new CreditAccountServiceImpl();
                creditAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank(),
                        bankService.getBank().getEmployees().get(1), paymentAccountService.getPayAcc(),
                        LocalDate.of(2022, 11, 11), 12, 150.0);

                userService.addPayAcc(paymentAccountService);
                userService.addCreditAcc(creditAccountService);
            }
            bankService.addUser(userService);
            banks.add(bankService);
            users.add(userService);
        }

        System.out.println("Bank");
        System.out.println(banks.get(0));
        System.out.println("\n\nClient");
        System.out.println(users.get(0));
    }

    static ArrayList<BankService> sortBanksByCriteria(ArrayList<BankService> banks, Double loanSum) {
        ArrayList<BankService> banksWithMoney = new ArrayList<>();
        ArrayList<Double> criteria = new ArrayList<>();
        for (BankService bank : banks) {
            if (bank.getBank().getMoney() >= loanSum) {
                banksWithMoney.add(bank);
                criteria.add(bank.getBank().getCountOffice() + bank.getBank().getCountATM() +
                        bank.getBank().getCountEmployees() + (20 - bank.getBank().getInterestRate()));
            }
        }
        for (int i = 0; i < criteria.size(); i++) {
            for (int j = 0; j < criteria.size(); j++) {
                if (criteria.get(j) < criteria.get(i)) {
                    Double crit = criteria.get(i);
                    BankService bank = banksWithMoney.get(i);

                    criteria.set(i, criteria.get(j));
                    banksWithMoney.set(i, banksWithMoney.get(j));
                    criteria.set(j, crit);
                    banksWithMoney.set(j, bank);
                }
            }
        }
        return banksWithMoney;
    }

    static void lab3() throws CredAccUserException, PayAccUserException, OfficeBankException, AtmBankException,
            EmployeeBankException, UserBankException, AtmOfficeException, EmployeeOfficeException, CreditExtension,
            BadUserRatingException {
        ArrayList<BankService> banks = new ArrayList<>();
        ArrayList<UserService> users = new ArrayList<>();
        for (int i_1 = 0; i_1 < 5; i_1++) {
            BankServiceImpl bankService = new BankServiceImpl();
            bankService.create(i_1, String.format("bank_???%d", i_1));
            for (int i_2 = 0; i_2 < 3; i_2++) {
                BankOfficeServiceImpl bankOfficeService = new BankOfficeServiceImpl();
                bankOfficeService.create(i_2 + i_1, String.format("office_???%d", i_2), bankService.getBank(),
                        String.format("address_%d", i_2), StatusOffice.Work, 15000.0);
                bankOfficeService.addMoney(bankService.getBank().getMoney() / 3);
                for (int i_3 = 0; i_3 < 5; i_3++) {
                    EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
                    employeeService.create(i_3 + 5 * i_2 + 3 * i_1, String.format("Oleg_%d", i_3 + 5 * i_2
                                    + 3 * i_1), "Yuriev",
                            LocalDate.of(2000, 10, 11), bankService.getBank(),
                            bankOfficeService.getBankOffice(), String.format("job_%d", i_3), 100.0);
                    bankOfficeService.addEmployee(employeeService);
                    bankService.addEmployee(employeeService);
                }
                AtmServiceImpl atmService = new AtmServiceImpl();
                atmService.create(i_2 + i_1, String.format("ATM_%d", i_2 + i_1), StatusATM.Work, Boolean.TRUE,
                        Boolean.TRUE,
                        100.0, bankOfficeService.getBankOffice().getBank(),
                        bankOfficeService.getBankOffice(), bankOfficeService.getBankOffice().getEmployees().get(1));
                atmService.addMoney(bankOfficeService.getBankOffice().getMoney());
                bankOfficeService.addBankATM(atmService);
                bankService.addBankATM(atmService);
                bankService.addBankOffice(bankOfficeService);
            }

            UserServiceImpl userService = new UserServiceImpl();
            userService.create(i_1, String.format("Elena_%d", i_1), "Romanovna", LocalDate.of(2000,
                    10, 11), String.format("work_%d", i_1));
            for (int i_2 = 0; i_2 < 2; i_2++) {
                PaymentAccountServiceImpl paymentAccountService = new PaymentAccountServiceImpl();
                paymentAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank());

                CreditAccountServiceImpl creditAccountService = new CreditAccountServiceImpl();
                creditAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank(),
                        bankService.getBank().getEmployees().get(1), paymentAccountService.getPayAcc(),
                        LocalDate.of(2022, 11, 11), 12, 150.0);

                userService.addPayAcc(paymentAccountService);
                userService.addCreditAcc(creditAccountService);
            }
            bankService.addUser(userService);
            banks.add(bankService);
            users.add(userService);
        }

        System.out.println("????????????");
        UserService workUser = users.get(0);
        System.out.println(workUser.getUser());
        System.out.println("\n?????????????? ?????????????????? ???????????? ??????????????");
        Scanner input = new Scanner(System.in);
        System.out.println("?????????????? ?????????? ??????????????: ");
        double loanSum = input.nextDouble();
        System.out.println("?????????????? ???????????????????? ??????????????: ");
        int countMonth = input.nextInt();
        ArrayList<BankService> banksWithMoney = sortBanksByCriteria(banks, loanSum);
        System.out.println("\n???????????????????????? ??????????:");
        for (int i = 0; i < banksWithMoney.size(); i++) {
            if (i != 0) {
                System.out.printf("\n???????? ???%d%n", i + 1);
            } else {
                System.out.printf("???????? ???%d%n", i + 1);
            }
            System.out.println(banksWithMoney.get(i).getBank());
        }
        System.out.println("\n???????????????? ???? ???????????????????????? ????????????: ");
        int bankID = input.nextInt();
        BankService workBank = banksWithMoney.get(bankID - 1);

        System.out.println("\n???????????????????????? ???????????????????? ??????????:");
        for (int i = 0; i < workBank.getBank().getOffices().size(); i++) {
            if (i != 0) {
                System.out.printf("\n???????? ???%d%n", i + 1);
            } else {
                System.out.printf("???????? ???%d%n", i + 1);
            }
            System.out.println(workBank.getBank().getOffices().get(i));
        }
        System.out.println("\n???????????????? ???? ???????????????????????? ????????????: ");
        int officeID = input.nextInt();
        BankOffice workOffice = workBank.getBank().getOffices().get(officeID - 1);

        System.out.println("\n???????????????????????? ????????????????????:");
        for (int i = 0; i < workOffice.getEmployees().size(); i++) {
            if (i != 0) {
                System.out.printf("\n?????????????????? ???%d%n", i + 1);
            } else {
                System.out.printf("?????????????????? ???%d%n", i + 1);
            }
            System.out.printf("id %d%n", workOffice.getEmployees().get(i).getId());
            System.out.printf("?????? %s", workOffice.getEmployees().get(i).getName());
            if (workOffice.getEmployees().get(i).getCanLend()) {
                System.out.println("\n?????????? ???????????????? ??????????????");
            } else {
                System.out.println("\n???? ?????????? ???????????????? ??????????????");
            }
        }
        System.out.println("\n???????????????? ???? ???????????????????????? ??????????????????????: ");
        int employeeID = input.nextInt();
        Employee workEmployee = workOffice.getEmployees().get(employeeID);
        //?????????? ????????????
        PaymentAccountServiceImpl payAcc = new PaymentAccountServiceImpl();
        CreditAccountServiceImpl creditAcc = new CreditAccountServiceImpl();
        workUser.applyForLoan(workBank, workOffice, workEmployee, workOffice.getBankATMS().get(0), loanSum,
                LocalDate.of(2022, 11, 11), countMonth, payAcc, creditAcc);
        System.out.println("???????????? ?????????????? ????????????????.");
        int size = workUser.getUser().getCreditAccounts().size();
        System.out.println(workUser.getUser().getCreditAccounts().get(size - 1));
    }

    static void lab4() throws CredAccUserException, PayAccUserException, OfficeBankException, AtmBankException,
            EmployeeBankException, UserBankException, AtmOfficeException, EmployeeOfficeException {
        ArrayList<BankService> banks = new ArrayList<>();
        ArrayList<UserService> users = new ArrayList<>();
        for (int i_1 = 0; i_1 < 5; i_1++) {
            BankServiceImpl bankService = new BankServiceImpl();
            bankService.create(i_1, String.format("bank_???%d", i_1));
            for (int i_2 = 0; i_2 < 3; i_2++) {
                BankOfficeServiceImpl bankOfficeService = new BankOfficeServiceImpl();
                bankOfficeService.create(i_2 + i_1, String.format("office_???%d", i_2), bankService.getBank(),
                        String.format("address_%d", i_2), StatusOffice.Work, 15000.0);
                bankOfficeService.addMoney(bankService.getBank().getMoney() / 3);
                for (int i_3 = 0; i_3 < 5; i_3++) {
                    EmployeeServiceImpl employeeService = new EmployeeServiceImpl();
                    employeeService.create(i_3 + 5 * i_2 + 3 * i_1, String.format("Pavel_%d", i_3 + 5 * i_2
                                    + 3 * i_1), "Kotlyarov",
                            LocalDate.of(2000, 10, 11), bankService.getBank(),
                            bankOfficeService.getBankOffice(), String.format("job_%d", i_3), 100.0);
                    bankOfficeService.addEmployee(employeeService);
                    bankService.addEmployee(employeeService);
                }
                AtmServiceImpl atmService = new AtmServiceImpl();
                atmService.create(i_2 + i_1, String.format("ATM_%d", i_2 + i_1), StatusATM.Work, Boolean.TRUE,
                        Boolean.TRUE,
                        100.0, bankOfficeService.getBankOffice().getBank(),
                        bankOfficeService.getBankOffice(), bankOfficeService.getBankOffice().getEmployees().get(1));
                atmService.addMoney(bankOfficeService.getBankOffice().getMoney());
                bankOfficeService.addBankATM(atmService);
                bankService.addBankATM(atmService);
                bankService.addBankOffice(bankOfficeService);
            }

            UserServiceImpl userService = new UserServiceImpl();
            userService.create(i_1, String.format("Nikita_%d", i_1), "Artymovich", LocalDate.of(2000,
                    10, 11), String.format("work_%d", i_1));
            for (int i_2 = 0; i_2 < 2; i_2++) {
                PaymentAccountServiceImpl paymentAccountService = new PaymentAccountServiceImpl();
                paymentAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank());

                CreditAccountServiceImpl creditAccountService = new CreditAccountServiceImpl();
                creditAccountService.create(i_2 + i_1, userService.getUser(), bankService.getBank(),
                        bankService.getBank().getEmployees().get(1), paymentAccountService.getPayAcc(),
                        LocalDate.of(2022, 11, 11), 12, 150.0);

                userService.addPayAcc(paymentAccountService);
                userService.addCreditAcc(creditAccountService);
            }
            bankService.addUser(userService);
            banks.add(bankService);
            users.add(userService);
        }
        try {
            users.get(0).saveToFile("file.txt", banks.get(0));
            System.out.println("?????????????????? ?????????? ???? ???????????? ?? ????????:");
            System.out.println(users.get(0).getUser().getPaymentAccounts());
            System.out.println("\n?????????????????? ?????????? ???? ???????????? ?? ????????:");
            System.out.println(users.get(0).getUser().getCreditAccounts());
            users.get(0).updateFromFile("file.txt");
            System.out.println("\n\n\n?????????????????? ?????????? ?????????? ???????????????????? ???? ??????????:");
            System.out.println(users.get(0).getUser().getPaymentAccounts());
            System.out.println("\n?????????????????? ?????????? ?????????? ???????????????????? ???? ??????????:");
            System.out.println(users.get(0).getUser().getCreditAccounts());
        } catch (IOException e) {
            System.out.println("???????????? ??????????: " + e);
        }
    }

    public static void main(String[] args) throws CredAccUserException, PayAccUserException, OfficeBankException,
            AtmBankException, EmployeeBankException, UserBankException, AtmOfficeException, EmployeeOfficeException, BadUserRatingException, CreditExtension {
        while (true) {
            System.out.println();
            System.out.print("Choose lab. For exit enter 0: ");
            Scanner in = new Scanner(System.in);
            int number = in.nextInt();

            switch (number) {
                case 0:
                    return;
                case 1:
                    lab1();
                    break;
                case 2:
                    lab2();
                    break;
                case 3:
                    lab3();
                    break;
                case 4:
                    lab4();
                    break;
                default:
                    System.out.println("Error: this lab is not defined. You should repeat....");
            }
        }

    }
}