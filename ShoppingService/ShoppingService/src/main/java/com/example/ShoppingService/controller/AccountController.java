package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.AccountNotFoundException;
import com.example.ShoppingService.model.Account;
import com.example.ShoppingService.model.Request.CreateAccountRequest;
import com.example.ShoppingService.model.Request.UpdateAccountPasswordRequest;
import com.example.ShoppingService.model.rowMapper.AccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.codec.digest.DigestUtils;


@RestController
public class AccountController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/createAccount", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Account createAccount(@RequestBody CreateAccountRequest request) {
        try {
            boolean accountExist = IsAccountExist(request.getLogin());
            if(accountExist){
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "ce nom d'utilisateur est déjà utilisé ! veuillez en choisir un autre");
            }
            String sql = "INSERT INTO accounts (login, password) VALUES (?,?);";
            String passhashed =  DigestUtils.sha256Hex(request.getPassword());
            int rows = jdbcTemplate.update(sql, request.getLogin(),passhashed);
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Impossible d'inserer un compte dans la base, veuillez réessayer ultérieurement.");
            }
            sql = "SELECT * FROM accounts WHERE login = ?";
            Account account = jdbcTemplate.queryForObject(sql, new AccountRowMapper(), request.getLogin());

            return  account;
        }catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("Compte crée mais impossible de le récuperer; réseesayer plus tard avec /getAccount");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    /**
     * Methode permettant de récuprer les informations de son compte, notamment l'id afin de pouvoir faire ses requetes.
     * @param request
     * @return
     */
    @GetMapping(value = "/getAccount", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Account getAccount(@RequestBody CreateAccountRequest request) {
        try {
            String sql = "SELECT * FROM accounts WHERE login = ?";
            Account account = jdbcTemplate.queryForObject(sql, new AccountRowMapper(), request.getLogin());
            String givenPassHashed = DigestUtils.sha256Hex(request.getPassword());
            if(!givenPassHashed.equals(account.getPassword())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mot de passe incorrect!");
            }
            return account;
        }catch (ResponseStatusException responseStatusException){
            throw  responseStatusException;
        }
        catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("Aucun compte trouvé pour le login " + request.getLogin());
        } catch (AccountNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    /***
     * Permet de mettre à jour son mot de passe
     * @param request
     */
    @PutMapping(value = "/updateAccountPassword", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void updateAccountPassword(@RequestBody UpdateAccountPasswordRequest request) {
        try {
            String sql = "SELECT * FROM accounts WHERE login = ?";
            Account account = jdbcTemplate.queryForObject(sql, new AccountRowMapper(), request.getLogin());
            String givenPassHashed = DigestUtils.sha256Hex(request.getActualPassword());
            if(!givenPassHashed.equals(account.getPassword())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mot de passe incorrect!");
            }


             sql = "UPDATE accounts SET password = ? WHERE login = ?";

            int rows = jdbcTemplate.update(sql, request.getNewPassword(), request.getLogin());
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Impossible de modifier le mot de passe, veuillez reeseayer ulterieurement :(");
            }
        }
        catch (EmptyResultDataAccessException e) {
            throw new AccountNotFoundException("Aucun compte trouvé pour le login " + request.getLogin());
        }
        catch (ResponseStatusException responseStatusException){
            throw  responseStatusException;
        }
        catch (AccountNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    private boolean IsAccountExist(String login) throws Exception {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM accounts WHERE login = ?)", Boolean.class, login);
    }
}
