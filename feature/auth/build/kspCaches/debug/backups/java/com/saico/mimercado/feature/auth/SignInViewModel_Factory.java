package com.saico.mimercado.feature.auth;

import com.saico.mimercado.core.domain.usecase.auth.AuthUseCases;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SignInViewModel_Factory implements Factory<SignInViewModel> {
  private final Provider<AuthUseCases> authUseCasesProvider;

  private SignInViewModel_Factory(Provider<AuthUseCases> authUseCasesProvider) {
    this.authUseCasesProvider = authUseCasesProvider;
  }

  @Override
  public SignInViewModel get() {
    return newInstance(authUseCasesProvider.get());
  }

  public static SignInViewModel_Factory create(Provider<AuthUseCases> authUseCasesProvider) {
    return new SignInViewModel_Factory(authUseCasesProvider);
  }

  public static SignInViewModel newInstance(AuthUseCases authUseCases) {
    return new SignInViewModel(authUseCases);
  }
}
