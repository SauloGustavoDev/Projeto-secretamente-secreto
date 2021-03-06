package saulodev.com.integrationproject.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import saulodev.com.integrationproject.R;
import saulodev.com.integrationproject.databinding.ActivityRegisterBinding;
import saulodev.com.integrationproject.ui.viewmodel.RegisterViewModel;
import saulodev.com.integrationproject.util.CpfCnpjUtils;
import saulodev.com.integrationproject.util.Mask;
import saulodev.com.integrationproject.util.MaskMoney;
import saulodev.com.integrationproject.util.VerificarDados;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding bind;
    private RegisterViewModel viewModel;
    private Drawable errorIcon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        errorIcon = ActivityCompat.getDrawable(this, R.drawable.ic_error);
        if (errorIcon != null)
            errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));


        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        maskField();
        edtWatchers();
        listeners();
    }

    private void maskField() {
        maskCpf();
        maskDate();
        maskMoney();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void listeners() {

        bind.entrarTxt.setOnClickListener(view -> {
            finish();
        });

        bind.voltarBtn.setOnClickListener(view -> {
            finish();
        });

        bind.termosCheckbox.setOnClickListener(view -> {
            if (bind.termosCheckbox.isChecked())
                bind.termosCheckbox.setError(null);
        });

        bind.registrarBtn.setOnClickListener(view -> {

            String nome = bind.nomeEdt.getText().toString().trim();
            String cpf = Mask.noMask(bind.cpfEdt.getText().toString().trim());
            String dataNascimento = bind.dataNascimentoEdt.getText().toString().trim();
            String email = bind.emailEdt.getText().toString().trim();
            String rendaMensal = MaskMoney.noMask(bind.rendaMensalEdt.getText().toString().trim());
            String patrimonioLiquido = MaskMoney.noMask(bind.patrimonioLiquidoEdt.getText().toString().trim());
            String senha = bind.senhaEdt.getText().toString().trim();
            String confirmarSenha = bind.confirmarSenhaEdt.getText().toString().trim();

            if (!nome.isEmpty() && !cpf.isEmpty() && !dataNascimento.isEmpty() &&
                    !email.isEmpty() && !rendaMensal.isEmpty() && !patrimonioLiquido.isEmpty() &&
                    !senha.isEmpty() && !confirmarSenha.isEmpty() && bind.termosCheckbox.isChecked()) {

                if (VerificarDados.validaCPF(cpf) && VerificarDados.dateIsValid(dataNascimento) &&
                        VerificarDados.validarEmail(email) && senha.equals(confirmarSenha)) {

                    if (senha.length() < 6 || confirmarSenha.length() < 6) {
                        bind.senhaTil.setError(getString(R.string.seis_digitos));
                        bind.confirmarSenhaTil.setError(getString(R.string.seis_digitos));
                    } else {
                        //TODO

                        Intent intent = new Intent(RegisterActivity.this, CadastroConcluidoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                } else {

                    if (!senha.equals(confirmarSenha))
                        bind.confirmarSenhaTil.setError(getString(R.string.senhas_iguais));

                    if (!VerificarDados.validarEmail(email))
                        bind.emailTil.setError(getString(R.string.email_invalido));

                    if (!VerificarDados.dateIsValid(dataNascimento))
                        bind.dataNascimentoTil.setError(getString(R.string.data_invalida_1));

                    if (!VerificarDados.validaCPF(cpf))
                        bind.cpfTil.setError(getString(R.string.cpf_invalido));
                }

            } else {
                if (!bind.termosCheckbox.isChecked())
                    bind.termosCheckbox.setError("Aceite os termos", errorIcon);

                if (confirmarSenha.isEmpty())
                    bind.confirmarSenhaTil.setError(getString(R.string.campo_obrigatorio));

                if (senha.isEmpty())
                    bind.senhaTil.setError(getString(R.string.campo_obrigatorio));

                if (patrimonioLiquido.isEmpty())
                    bind.patrimonioLiquidoTil.setError(getString(R.string.campo_obrigatorio));

                if (rendaMensal.isEmpty())
                    bind.rendaMensalTil.setError(getString(R.string.campo_obrigatorio));

                if (email.isEmpty())
                    bind.emailTil.setError(getString(R.string.campo_obrigatorio));

                if (dataNascimento.isEmpty())
                    bind.dataNascimentoTil.setError(getString(R.string.campo_obrigatorio));

                if (cpf.isEmpty())
                    bind.cpfTil.setError(getString(R.string.campo_obrigatorio));

                if (nome.isEmpty())
                    bind.nomeTil.setError(getString(R.string.campo_obrigatorio));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void edtWatchers() {

        bind.nomeEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.nomeEdt.getText().toString().trim().isEmpty())
                bind.nomeTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.nomeTil.setErrorEnabled(false);
        });

        bind.cpfEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String cpf = Mask.noMask(bind.cpfEdt.getText().toString().trim());

                if (cpf.length() == 11) {
                    if (!VerificarDados.validaCPF(cpf))
                        bind.cpfTil.setError(getString(R.string.cpf_invalido));
                    else
                        bind.cpfTil.setErrorEnabled(false);
                } else
                    bind.cpfTil.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bind.cpfEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.cpfEdt.getText().toString().trim().isEmpty())
                bind.cpfTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.cpfTil.setErrorEnabled(false);

        });

        bind.dataNascimentoEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String data = bind.dataNascimentoEdt.getText().toString().trim();

                if (data.length() == 10) {
                    if (!VerificarDados.dateIsValid(data)) {
                        bind.dataNascimentoTil.setError(getString(R.string.data_invalida_1));
                    } else {
                        if (VerificarDados.calculoIdade(data) < 18) {
                            bind.dataNascimentoTil.setError(getString(R.string.data_invalida_2));
                        } else
                            bind.dataNascimentoTil.setErrorEnabled(false);
                    }
                } else {
                    bind.dataNascimentoTil.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bind.dataNascimentoEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.dataNascimentoEdt.getText().toString().trim().isEmpty())
                bind.dataNascimentoTil.setError(getString(R.string.campo_obrigatorio));

        });

        bind.emailEdt.setOnFocusChangeListener((view, hasFocus) -> {
            String email = bind.emailEdt.getText().toString().trim();

            if (!hasFocus && email.isEmpty()) {
                bind.emailTil.setError(getString(R.string.campo_obrigatorio));
            } else if (hasFocus && email.isEmpty()) {
                bind.emailTil.setErrorEnabled(false);
            } else {
                if (!VerificarDados.validarEmail(email)) {
                    bind.emailTil.setError(getString(R.string.email_invalido));
                } else
                    bind.emailTil.setErrorEnabled(false);
            }
        });

        bind.rendaMensalEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.rendaMensalEdt.getText().toString().trim().isEmpty())
                bind.rendaMensalTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.rendaMensalTil.setErrorEnabled(false);
        });

        bind.patrimonioLiquidoEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.patrimonioLiquidoEdt.getText().toString().trim().isEmpty())
                bind.patrimonioLiquidoTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.patrimonioLiquidoTil.setErrorEnabled(false);
        });


        bind.senhaEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.senhaEdt.getText().toString().trim().isEmpty())
                bind.senhaTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.senhaTil.setErrorEnabled(false);
        });

        bind.confirmarSenhaEdt.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus && bind.confirmarSenhaEdt.getText().toString().trim().isEmpty())
                bind.confirmarSenhaTil.setError(getString(R.string.campo_obrigatorio));
            else
                bind.confirmarSenhaTil.setErrorEnabled(false);
        });
    }

    private void maskDate() {
        bind.dataNascimentoEdt.addTextChangedListener(
                Mask.insert(Mask.DATA_MASK, bind.dataNascimentoEdt));
    }

    private void maskCpf() {
        bind.cpfEdt.addTextChangedListener(
                Mask.insert(Mask.CPF_MASK, bind.cpfEdt));
    }

    private void maskMoney() {
        Locale locale = new Locale("pt", "BR");
        bind.rendaMensalEdt.addTextChangedListener(new MaskMoney(bind.rendaMensalEdt, locale));
        bind.patrimonioLiquidoEdt.addTextChangedListener(new MaskMoney(bind.patrimonioLiquidoEdt, locale));
    }
}