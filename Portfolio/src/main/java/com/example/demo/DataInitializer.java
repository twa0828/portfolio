package com.example.demo;

import com.example.demo.model.Category;
import com.example.demo.model.Contact;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ContactRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer
        implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ContactRepository contactRepository;
    private final AccountRepository accountRepository;

    public DataInitializer(
            CategoryRepository categoryRepository,
            ContactRepository contactRepository,
            AccountRepository accountRepository) {

        this.categoryRepository = categoryRepository;
        this.contactRepository = contactRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(
            String... args) {

        if (categoryRepository.count() == 0) {

            categoryRepository.save(new Category(
                    "不具合"));

            categoryRepository.save(new Category(
                    "要望"));

            categoryRepository.save(new Category(
                    "質問"));
        }

        if (contactRepository.count() == 0) {

            contactRepository.save(new Contact(
                    "不具合",
                    "ログインできません。\nメールアドレスとパスワードを入力してもログイン画面に戻ってしまいます。",
                    "未対応"));

            contactRepository.save(new Contact(
                    "要望",
                    "ランキング機能を追加してほしいです。\n年間ランキングだけでなく、週間ランキングも見たいです。",
                    "対応中"));

            contactRepository.save(new Contact(
                    "質問",
                    "退会方法について教えてください。\nマイページから操作できますか？",
                    "対応済み"));
        }

        if (accountRepository.count() == 0) {

            accountRepository.save(new Account(
                    "ADMIN",
                    "田中",
                    "tanaka@test.com",
                    "password1",
                    "アクセス許可",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "佐藤",
                    "sato@test.com",
                    "password1",
                    "アクセス禁止",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "鈴木",
                    "suzuki@test.com",
                    "password1",
                    "アクセス許可",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "高橋",
                    "takahashi@test.com",
                    "password1",
                    "アクセス許可",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "伊藤",
                    "ito@test.com",
                    "password1",
                    "アクセス禁止",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "渡辺",
                    "watanabe@test.com",
                    "password1",
                    "アクセス許可",
                    "false"));

            accountRepository.save(new Account(
                    "ADMIN",
                    "山本",
                    "yamamoto@test.com",
                    "password1",
                    "アクセス禁止",
                    "false"));
        }
    }
}
